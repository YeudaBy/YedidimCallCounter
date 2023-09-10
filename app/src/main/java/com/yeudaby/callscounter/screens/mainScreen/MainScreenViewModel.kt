package com.yeudaby.callscounter.screens.mainScreen

import android.content.ContentResolver
import android.content.Context
import android.provider.CallLog
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import java.util.Calendar

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState get() = _uiState

    fun init(context: Context) = viewModelScope.launch {
        e("init")
        _uiState.value = _uiState.value.copy(
            calls = emptyList(),
            filteredCalls = emptyList(),
        )
        getCallsLog(
            fromDateMillis = getMonthsAgo(1),
            toDateMillis = System.currentTimeMillis(),
            context = context,
        )
    }

    fun refresh(context: Context) = viewModelScope.launch {
        e("refresh")
        _uiState.value = _uiState.value.copy(
            calls = emptyList(),
            filteredCalls = emptyList(),
        )
        getCallsLog(
            fromDateMillis = getMonthsAgo(1),
            toDateMillis = System.currentTimeMillis(),
            context = context,
        )
    }

    fun onDurationCheckedChange(
        isChecked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            withDuration = isChecked,
        )
        filterCalls()
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

    fun getStats(): List<Stat> {
        return listOf(
            Stat(
                label = R.string.last_hour,
                count = filterByDate(getHoursAgo(1)).size,
                color = statsColors["hour"]!!,
            ),
            Stat(
                label = R.string.last_2_hours,
                count = filterByDate(getHoursAgo(2)).size,
                color = statsColors["hour"]!!,
            ),
            Stat(
                label = R.string.last_day,
                count = filterByDate(getDaysAgo(1)).size,
                color = statsColors["day"]!!,
            ),
            Stat(
                label = R.string.last_2_days,
                count = filterByDate(getDaysAgo(2)).size,
                color = statsColors["day"]!!,
            ),
            Stat(
                label = R.string.last_week,
                count = filterByDate(getWeeksAgo(1)).size,
                color = statsColors["week"]!!,
            ),
            Stat(
                label = R.string.last_2_weeks,
                count = filterByDate(getWeeksAgo(2)).size,
                color = statsColors["week"]!!,
            ),
            Stat(
                label = R.string.last_month,
                count = filterByDate(getMonthsAgo(1)).size,
                color = statsColors["month"]!!,
            )
        )
    }

    private val statsColors = mapOf<String, Color>(
        "hour" to Color(0xFF9C27B0),
        "day" to Color(0xFFFF5722),
        "week" to Color(0xFF4CAF50),
        "month" to Color(0xFFFFEB3B),
    )

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
                        newCallLog.also { i("CallLogEntry: $it") }
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
        val withDuration = _uiState.value.withDuration
        val selectedCallTypes = _uiState.value.selectedCallTypes
        return selectedCallTypes.contains(callLogEntry.type) && if (withDuration) {
            callLogEntry.duration >= 15
        } else {
            true
        } && callLogEntry.number in listOf("1230", "0533131310")
    }

    private fun getDaysAgo(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.timeInMillis
    }

    private fun getWeeksAgo(weeksAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksAgo)
        return calendar.timeInMillis
    }

    private fun getHoursAgo(hoursAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hoursAgo)
        return calendar.timeInMillis
    }

    private fun getMonthsAgo(monthsAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -monthsAgo)
        return calendar.timeInMillis
    }

    private fun filterByDate(
        date: Long,
    ): List<CallLogEntry> {
        return _uiState.value.filteredCalls.filter {
            it.date >= date
        }
    }

    data class Stat(
        val count: Int,
        @StringRes val label: Int,
        val color: Color,
    )
}