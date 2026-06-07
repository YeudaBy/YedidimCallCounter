package com.yeudaby.callscounter.data.calls

import com.yeudaby.callscounter.data.model.CallLogEntry

interface CallsResolver {
    suspend fun queryCalls(
        fromDateMillis: Long,
        toDateMillis: Long,
    ): List<CallLogEntry>
}