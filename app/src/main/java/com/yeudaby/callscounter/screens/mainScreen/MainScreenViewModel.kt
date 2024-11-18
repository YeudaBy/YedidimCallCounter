package com.yeudaby.callscounter.screens.mainScreen

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.CallLog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.data.model.DataItem
import com.yeudaby.callscounter.data.model.Statistics
import com.yeudaby.callscounter.data.model.Statistics.Companion.statsColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.Forest.i
import java.util.Calendar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainScreenViewModel : ViewModel() {

    private val SECONDS_KEY = intPreferencesKey("seconds")

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState get() = _uiState

    fun init(context: Context) {
        val secondsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
            preferences[SECONDS_KEY] ?: 0
        }

        viewModelScope.launch {
            secondsFlow.collect {
                Timber.w("seconds: $it")
                _uiState.value = _uiState.value.copy(
                    fromDuration = it
                )
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                calls = emptyList(),
                filteredCalls = emptyList(),
            )
            getCallsLog(
                fromDateMillis = getMonthsAgo(1),
                toDateMillis = System.currentTimeMillis(),
                context = context,
            )
            _uiState.value = _uiState.value.copy(
                statistics = getStatistics(),
                data = getData(),
            )
        }
    }

    fun shareApp(context: Context) {
        val text = buildString {
            append(context.getString(R.string.share_text))
            append("\n\n")
            append(context.getString(R.string.latest_releases_url))
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }


    fun onDurationChange(
        duration: Int, context: Context
    ) {
        _uiState.value = _uiState.value.copy(
            fromDuration = duration,
        )
        filterCalls()
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[SECONDS_KEY] = duration
            }
        }
    }

    fun onCallTypeCheckedChange(
        callType: CallType
    ) {
        val currentSelectedCallTypes = _uiState.value.selectedCallTypes
        if (!currentSelectedCallTypes.contains(callType)) {
            _uiState.value = _uiState.value.copy(
                selectedCallTypes = currentSelectedCallTypes.plus(callType),
            )
        } else {
            _uiState.value = _uiState.value.copy(
                selectedCallTypes = currentSelectedCallTypes.minus(callType),
            )
        }
        filterCalls()
    }

    private fun getData(): List<DataItem> {
        return listOf(
            DataItem(
                label = R.string.start_of_hour,
                count = filterByDate(getFromStartOfTheHour()).size,
                color = statsColors["hour"]!!,
                fromMillis = getFromStartOfTheHour(),
            ), DataItem(
                label = R.string.last_hour,
                count = filterByDate(getHoursAgo(1)).size,
                color = statsColors["hour"]!!,
                fromMillis = getHoursAgo(1),
            ), DataItem(
                label = R.string.start_of_day,
                count = filterByDate(getFromStartOfTheDay()).size,
                color = statsColors["day"]!!,
                fromMillis = getFromStartOfTheDay(),
            ), DataItem(
                label = R.string.last_day,
                count = filterByDate(getDaysAgo(1)).size,
                color = statsColors["day"]!!,
                fromMillis = getDaysAgo(1),
            ), DataItem(
                label = R.string.start_of_week,
                count = filterByDate(getFromStartOfTheWeek()).size,
                color = statsColors["week"]!!,
                fromMillis = getFromStartOfTheWeek(),
            ), DataItem(
                label = R.string.last_week,
                count = filterByDate(getWeeksAgo(1)).size,
                color = statsColors["week"]!!,
                fromMillis = getWeeksAgo(1),
            ), DataItem(
                label = R.string.start_of_month,
                count = filterByDate(getFromStartOfTheMonth()).size,
                color = statsColors["month"]!!,
                fromMillis = getFromStartOfTheMonth(),
            ), DataItem(
                label = R.string.last_month,
                count = filterByDate(getMonthsAgo(1)).size,
                color = statsColors["month"]!!,
                fromMillis = getMonthsAgo(1),
            )
        )
    }

    private fun getStatistics(): Statistics? {
        val filteredCalls = _uiState.value.filteredCalls
        val longestCall = filteredCalls.maxByOrNull { it.duration } ?: return null
        val mostBusiestHour = filteredCalls.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }.maxByOrNull { it.value.size }!!.key
        val totalDurationStartOfHour = filteredCalls.filter {
            it.date >= getFromStartOfTheHour()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfDay = filteredCalls.filter {
            it.date >= getFromStartOfTheDay()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfWeek = filteredCalls.filter {
            it.date >= getFromStartOfTheWeek()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfMonth = filteredCalls.filter {
            it.date >= getFromStartOfTheMonth()
        }.sumBy { it.duration.toInt() }
        return Statistics(
            longestCall = longestCall,
            mostBusiestHour = mostBusiestHour,
            totalDurationStartOfHour = totalDurationStartOfHour,
            totalDurationStartOfDay = totalDurationStartOfDay,
            totalDurationStartOfWeek = totalDurationStartOfWeek,
            totalDurationStartOfMonth = totalDurationStartOfMonth,
        )
    }


    private fun filterCalls() {
        val filteredCalls = _uiState.value.calls.filter { isValidCall(it) }
        _uiState.value = _uiState.value.copy(
            filteredCalls = filteredCalls,
        )
    }

    private fun getCallsLog(
        fromDateMillis: Long,
        toDateMillis: Long,
        context: Context,
    ) {
        val contentResolver: ContentResolver = context.contentResolver
        val selection =
            "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DATE} <= ?"
        val selectionArgs =
            arrayOf(fromDateMillis.toString(), toDateMillis.toString())
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        val projection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
        )
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
                val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION))
                val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                    CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                    else -> CallType.MISSED
                }

                val newCallLog = CallLogEntry(
                    date = date,
                    number = number,
                    duration = duration,
                    type = callType,
                )

                _uiState.value = _uiState.value.copy(
                    calls = _uiState.value.calls.plus(
                        newCallLog.also {
                            i("CallLogEntry: $it")
                        }
                    )
                )
            }
            cursor.close()
        }
        filterCalls()
    }

    private fun isValidCall(
        callLogEntry: CallLogEntry,
    ): Boolean {
        val fromDuration = _uiState.value.fromDuration.takeIf { it > 0 }
        val selectedCallTypes = _uiState.value.selectedCallTypes
        return selectedCallTypes
            .contains(callLogEntry.type)
                && if (fromDuration != null) {
            callLogEntry.duration >= fromDuration
        } else {
            true
        } && callLogEntry.number in listOf("1230", "0533131310")
    }


    private fun getHoursAgo(hoursAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hoursAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheHour(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    private fun getDaysAgo(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.timeInMillis
    }

    private fun getWeeksAgo(weeksAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheWeek(): Long {
        val calendar = Calendar.getInstance()

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = (currentDayOfWeek - Calendar.FRIDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }


    private fun getMonthsAgo(monthsAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -monthsAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.timeInMillis
    }

    private fun filterByDate(
        date: Long,
    ): List<CallLogEntry> {
        return _uiState.value.filteredCalls.filter {
            it.date >= date
        }
    }
}
