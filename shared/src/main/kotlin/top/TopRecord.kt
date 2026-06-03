package com.yeudaby.calls_counter.shared.top

import com.yeudaby.calls_counter.call_record.CallRecord
import com.yeudaby.calls_counter.shared.dispatcher.Dispatcher

data class TopRecord(
    val dispatcher: Dispatcher,
    val calls: List<CallRecord>,
)
