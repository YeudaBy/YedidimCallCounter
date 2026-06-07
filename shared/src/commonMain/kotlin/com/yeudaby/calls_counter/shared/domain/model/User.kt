package com.yeudaby.calls_counter.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Region {
    JERUSALEM, NORTH, SOUTH, CENTER
}

@Serializable
enum class Role {
    REGULAR, HIGH_SCHOOL_STUDENT, SHIFT_MANAGER, MANAGER
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val region: Region,
    val dispatcherNumber: String,
    val role: Role,
    val fcmToken: String? = null,
    val weeklyGoal: Int = 40,
    val isAnonymous: Boolean = false
)
