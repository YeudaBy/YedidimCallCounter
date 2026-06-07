package com.yeudaby.callscounter.screens.mainScreen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CallLog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.calls.CallsResolverImpl
import com.yeudaby.callscounter.data.calls.TrackedCallMatcher
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.data.model.DataItem
import com.yeudaby.callscounter.data.model.Statistics
import com.yeudaby.callscounter.data.model.Statistics.Companion.statsColors
import com.yeudaby.callscounter.data.repository.AppSettingsRepository
import com.yeudaby.callscounter.getIntOrDefault
import com.yeudaby.callscounter.getLongOrDefault
import com.yeudaby.callscounter.getStringOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import kotlin.math.roundToInt

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainScreenViewModel : ViewModel() {

    private val SECONDS_KEY = intPreferencesKey("seconds")

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState get() = _uiState

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        val appContext = context.applicationContext
        val repository = AppSettingsRepository.getInstance(appContext)

        viewModelScope.launch {
            repository.observeWeekGoal().collect { goal ->
                _uiState.update { it.copy(weekGoal = goal) }
            }
        }

        // Observe DataStore for duration preference changes
        viewModelScope.launch {
            context.dataStore.data.map { it[SECONDS_KEY] ?: 0 }.collect { seconds ->
                _uiState.update { it.copy(fromDuration = seconds) }
                if (_uiState.value.calls.isNotEmpty()) filterAndUpdate()
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = loadCalls(
                fromDateMillis = getMonthsAgo(CALL_HISTORY_MONTHS),
                toDateMillis = System.currentTimeMillis(),
                context = context,
            )
            _uiState.update { it.copy(calls = result.calls, callLogHint = result.hintLabel) }
            filterAndUpdate()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun shareApp(context: Context) {
        val text = buildString {
            append(context.getString(R.string.share_text))
            append("\n\n")
            append(context.getString(R.string.latest_releases_url))
        }
        context.startActivity(
            Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                },
                null
            )
        )
    }

    fun shareWeekProgress(context: Context) {
        val state = _uiState.value
        val safeWeekGoal = state.weekGoal.coerceAtLeast(1)
        val progressPercent = ((state.weekCount.toFloat() / safeWeekGoal.toFloat()) * 100)
            .roundToInt()
            .coerceAtMost(100)
        val shareText = context.getString(
            if (state.weekCount >= safeWeekGoal) {
                R.string.week_goal_share_reached_text
            } else {
                R.string.week_goal_share_text
            },
            state.weekCount,
            safeWeekGoal,
            progressPercent,
        )

        context.startActivity(
            Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                },
                null,
            )
        )
    }

    fun onDurationChange(duration: Int, context: Context) {
        _uiState.update { it.copy(fromDuration = duration) }
        filterAndUpdate()
        viewModelScope.launch {
            context.dataStore.edit { it[SECONDS_KEY] = duration }
        }
    }

    fun onCallTypeCheckedChange(callType: CallType) {
        val current = _uiState.value.selectedCallTypes
        _uiState.update {
            it.copy(
                selectedCallTypes = if (callType in current) current - callType else current + callType
            )
        }
        filterAndUpdate()
    }

    private fun filterAndUpdate() {
        val filteredCalls = _uiState.value.calls.filter { isValidCall(it) }
        val data = computeData(filteredCalls)
        _uiState.update {
            it.copy(
                filteredCalls = filteredCalls,
                statistics = computeStatistics(filteredCalls),
                data = data,
                todayCount = data.firstOrNull { item -> item.label == R.string.start_of_day }?.count ?: 0,
                weekCount  = data.firstOrNull { item -> item.label == R.string.start_of_week }?.count ?: 0,
            )
        }
    }

    private fun computeData(filteredCalls: List<CallLogEntry>): List<DataItem> {
        val startOfHour = getFromStartOfTheHour()
        val oneHourAgo = getHoursAgo(1)
        val startOfDay = getFromStartOfTheDay()
        val oneDayAgo = getDaysAgo(1)
        val startOfWeek = getFromStartOfTheWeek()
        val oneWeekAgo = getWeeksAgo(1)
        val startOfMonth = getFromStartOfTheMonth()
        val oneMonthAgo = getMonthsAgo(1)

        fun countFrom(from: Long) = filteredCalls.count { it.date >= from }

        return listOf(
            DataItem(label = R.string.start_of_hour, count = countFrom(startOfHour), color = statsColors["hour"]!!, fromMillis = startOfHour),
            DataItem(label = R.string.last_hour, count = countFrom(oneHourAgo), color = statsColors["hour"]!!, fromMillis = oneHourAgo),
            DataItem(label = R.string.start_of_day, count = countFrom(startOfDay), color = statsColors["day"]!!, fromMillis = startOfDay),
            DataItem(label = R.string.last_day, count = countFrom(oneDayAgo), color = statsColors["day"]!!, fromMillis = oneDayAgo),
            DataItem(label = R.string.start_of_week, count = countFrom(startOfWeek), color = statsColors["week"]!!, fromMillis = startOfWeek),
            DataItem(label = R.string.last_week, count = countFrom(oneWeekAgo), color = statsColors["week"]!!, fromMillis = oneWeekAgo),
            DataItem(label = R.string.start_of_month, count = countFrom(startOfMonth), color = statsColors["month"]!!, fromMillis = startOfMonth),
            DataItem(label = R.string.last_month, count = countFrom(oneMonthAgo), color = statsColors["month"]!!, fromMillis = oneMonthAgo),
        )
    }

    private fun computeStatistics(filteredCalls: List<CallLogEntry>): Statistics? {
        if (filteredCalls.isEmpty()) return null
        val longestCall = filteredCalls.maxByOrNull { it.duration } ?: return null
        val mostBusiestHour = filteredCalls
            .groupBy { truncateToHour(it.date) }
            .maxByOrNull { it.value.size }
            ?.key ?: return null
        return Statistics(
            longestCall = longestCall,
            mostBusiestHour = mostBusiestHour,
            totalDurationStartOfHour = filteredCalls.filter { it.date >= getFromStartOfTheHour() }.sumOf { it.duration.toInt() },
            totalDurationStartOfDay = filteredCalls.filter { it.date >= getFromStartOfTheDay() }.sumOf { it.duration.toInt() },
            totalDurationStartOfWeek = filteredCalls.filter { it.date >= getFromStartOfTheWeek() }.sumOf { it.duration.toInt() },
            totalDurationStartOfMonth = filteredCalls.filter { it.date >= getFromStartOfTheMonth() }.sumOf { it.duration.toInt() },
        )
    }

    private fun truncateToHour(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun isValidCall(entry: CallLogEntry): Boolean {
        val minDuration = _uiState.value.fromDuration.takeIf { it > 0 }
        return entry.type in _uiState.value.selectedCallTypes
                && (minDuration == null || entry.duration >= minDuration)
                && TrackedCallMatcher.matches(entry.number, TRACKED_NUMBERS)
    }

    private fun isPlausibleCall(entry: CallLogEntry): Boolean {
        return entry.date > 0L && entry.number.isNotBlank()
    }

    private fun callDedupKey(entry: CallLogEntry): String {
        val dedupedTimestamp = entry.date / 1000L
        val numberKey = TrackedCallMatcher.comparableKey(entry.number)
        return listOf(
            dedupedTimestamp.toString(),
            numberKey,
            entry.duration.toString(),
            entry.type.name,
        ).joinToString("|")
    }

    private fun getHoursAgo(n: Int) = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -n) }.timeInMillis
    private fun getFromStartOfTheHour() = Calendar.getInstance().apply {
        set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    private fun getDaysAgo(n: Int) = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -n) }.timeInMillis
    private fun getFromStartOfTheDay() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    private fun getWeeksAgo(n: Int) = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, -n) }.timeInMillis
    private fun getFromStartOfTheWeek(): Long {
        val calendar = Calendar.getInstance()
        val daysToSubtract = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.FRIDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    private fun getMonthsAgo(n: Int) = Calendar.getInstance().apply { add(Calendar.MONTH, -n) }.timeInMillis
    private fun getFromStartOfTheMonth() = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    companion object {
        private const val CALL_HISTORY_MONTHS = 12
        private const val FUTURE_CALL_TOLERANCE_MS = 60_000L
        private val TRACKED_NUMBERS = setOf("1230", "0533131310")
    }
}

private data class LoadedCalls(
    val calls: List<CallLogEntry>,
    val hintLabel: Int?,
)
