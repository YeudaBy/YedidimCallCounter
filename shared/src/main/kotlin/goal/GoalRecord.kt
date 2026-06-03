package com.yeudaby.calls_counter.shared.goal

data class GoalRecord(
    val type: GoalType,
    val goal: Int,
    val accomplished: Boolean,
    val hidden: Boolean,
)
