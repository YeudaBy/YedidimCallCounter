package com.yeudaby.callscounter.screens.mainScreen

import androidx.annotation.StringRes
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.data.model.DataItem
import com.yeudaby.callscounter.data.model.Statistics
import java.util.Collections.emptyList

data class MainScreenUiState(
    val calls: List<CallLogEntry> = emptyList(),
    val filteredCalls: List<CallLogEntry> = emptyList(),
    val fromDuration: Int = 0,
    val selectedCallTypes: List<CallType> = listOf(CallType.INCOMING),
    val statistics: Statistics? = null,
    val data: List<DataItem> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val callLogHint: Int? = null,
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val weekGoal: Int = 15,
)
