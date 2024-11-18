package com.yeudaby.callscounter.screens.mainScreen

import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.data.model.DataItem
import com.yeudaby.callscounter.data.model.Statistics

data class MainScreenUiState(
    val calls: List<CallLogEntry> = emptyList(),
    val filteredCalls: List<CallLogEntry> = emptyList(),
    val fromDuration: Int = 0,
    val selectedCallTypes: List<CallType> = listOf(CallType.INCOMING),
    val statistics: Statistics? = null,
    val data: List<DataItem> = emptyList()
)