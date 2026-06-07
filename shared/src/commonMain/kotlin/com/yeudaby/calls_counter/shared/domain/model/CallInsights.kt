package com.yeudaby.calls_counter.shared.domain.model

data class CallInsights(
    val totalCallsThisWeek: Int,
    val longestCallDurationSeconds: Int,
    val bestHourOfDay: Int?,
    val bestDayOfWeek: Int?,
    val averageDurationSeconds: Int
)
