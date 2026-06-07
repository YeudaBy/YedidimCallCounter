package com.yeudaby.callscounter.data.model

import kotlin.time.Duration

data class CallLogEntry(
    val date: Long,
    val number: String,
    val duration: Duration,
    val type: CallType,
) {
    override fun toString(): String {
        return "CallLogEntry(date=$date, number='$number', duration=$duration, type=$type)"
    }
}