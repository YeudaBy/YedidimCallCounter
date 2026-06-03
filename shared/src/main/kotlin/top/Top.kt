package com.yeudaby.calls_counter.shared.top

import shared.top.District

data class Top(
    val district: District? = null,
    val topRecords: List<TopRecord> = emptyList(),
)
