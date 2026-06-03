package com.yeudaby.calls_counter.shared.dispatcher

import com.yeudaby.calls_counter.shared.goal.GoalRecord
import shared.top.District

data class Dispatcher(
    val district: District,
    val name: String,
    val id: Int,
    val isTrainee: Boolean,
    val isStudent: Boolean,
    val goals: List<GoalRecord> = emptyList(),
)
