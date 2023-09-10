package com.yeudaby.callscounter.data.model

data class CallLogEntry(
    val date: Long,
    val number: String,
    val duration: Long,
    val type: CallType,
) {
    override fun toString(): String {
        return "CallLogEntry(date=$date, number='$number', duration=$duration, type=$type)"
    }
}