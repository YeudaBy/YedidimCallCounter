package com.yeudaby.calls_counter.shared.domain.usecase

import com.yeudaby.calls_counter.shared.domain.model.CallInsights
import com.yeudaby.calls_counter.shared.domain.model.CallRecord
import com.yeudaby.calls_counter.shared.domain.model.CallType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AnalyzeCallInsightsUseCase {
    operator fun invoke(records: List<CallRecord>): CallInsights {
        val incomingCalls = records.filter { it.type == CallType.INCOMING }
        
        if (incomingCalls.isEmpty()) {
            return CallInsights(0, 0, null, null, 0)
        }

        val totalCallsThisWeek = incomingCalls.size
        val longestCallDurationSeconds = incomingCalls.maxOfOrNull { it.durationSeconds } ?: 0
        val averageDurationSeconds = if (totalCallsThisWeek > 0) {
            incomingCalls.sumOf { it.durationSeconds } / totalCallsThisWeek
        } else 0

        val callsPerHour = mutableMapOf<Int, Int>()
        val callsPerDay = mutableMapOf<Int, Int>()

        val timeZone = TimeZone.currentSystemDefault()
        
        incomingCalls.forEach { record ->
            val localDateTime = Instant.fromEpochMilliseconds(record.timestamp).toLocalDateTime(timeZone)
            
            val hour = localDateTime.hour
            callsPerHour[hour] = callsPerHour.getOrElse(hour) { 0 } + 1
            
            val dayOfWeek = localDateTime.dayOfWeek.value
            callsPerDay[dayOfWeek] = callsPerDay.getOrElse(dayOfWeek) { 0 } + 1
        }

        val bestHourOfDay = callsPerHour.maxByOrNull { it.value }?.key
        val bestDayOfWeek = callsPerDay.maxByOrNull { it.value }?.key

        return CallInsights(
            totalCallsThisWeek = totalCallsThisWeek,
            longestCallDurationSeconds = longestCallDurationSeconds,
            bestHourOfDay = bestHourOfDay,
            bestDayOfWeek = bestDayOfWeek,
            averageDurationSeconds = averageDurationSeconds
        )
    }
}
