package com.yeudaby.callscounter.screens.mainScreen

import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType

data class MainScreenUiState(
    val calls: List<CallLogEntry> = emptyList(),
    val filteredCalls: List<CallLogEntry> = emptyList(),
    val withDuration: Boolean = false,
    val selectedCallTypes: List<CallType> = listOf(CallType.INCOMING),
    val statistics: MainScreenViewModel.Statistics?,
    val data: List<MainScreenViewModel.DataItem> = emptyList()
)