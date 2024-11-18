package com.yeudaby.callscounter.data.model

import androidx.compose.ui.graphics.Color

data class Statistics(
    val longestCall: CallLogEntry,
    val mostBusiestHour: Long,
    val totalDurationStartOfHour: Int,
    val totalDurationStartOfDay: Int,
    val totalDurationStartOfWeek: Int,
    val totalDurationStartOfMonth: Int,
) {

    companion object {
        val statsColors = mapOf(
            "hour" to Color(0xFF9C27B0),
            "day" to Color(0xFFFF5722),
            "week" to Color(0xFF4CAF50),
            "month" to Color(0xFFFFEB3B),
        )
    }
}