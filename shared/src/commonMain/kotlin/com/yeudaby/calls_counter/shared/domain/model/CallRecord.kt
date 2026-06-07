package com.yeudaby.calls_counter.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CallType {
    INCOMING, OUTGOING, MISSED, REJECTED
}

@Serializable
data class CallRecord(
    val id: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val type: CallType,
    val number: String
)
