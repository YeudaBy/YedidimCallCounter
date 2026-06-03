package com.yeudaby.calls_counter.shared.call

import com.yeudaby.calls_counter.system.HotlineNumber
import kotlin.time.Duration

data class CallRecord(
    val timestamp: Long,
    val origin: HotlineNumber,
    val duration: Duration,
    val direction: CallDirection
)
