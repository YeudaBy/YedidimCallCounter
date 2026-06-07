package com.yeudaby.callscounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.calls_counter.shared.domain.model.CallInsights
import com.yeudaby.calls_counter.shared.domain.repository.CallsRepository
import com.yeudaby.calls_counter.shared.domain.usecase.AnalyzeCallInsightsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

data class HomeUiState(
    val isLoading: Boolean = true,
    val callsCount: Int = 0,
    val insights: CallInsights? = null,
    val error: String? = null
)

class HomeViewModel(
    private val callsRepository: CallsRepository,
    private val analyzeCallInsightsUseCase: AnalyzeCallInsightsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    // Fallback simple calc for start of week
    private val fromTimestamp: Long = Clock.System.now()
        .minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

    private val callRecordsFlow = callsRepository.getCallRecordsFlow(fromTimestamp)

    val uiState: StateFlow<HomeUiState> = combine(
        _isLoading,
        _error,
        callRecordsFlow
    ) { isLoading, error, records ->
        val insights = analyzeCallInsightsUseCase(records)
        HomeUiState(
            isLoading = false,
            callsCount = insights.totalCallsThisWeek,
            insights = insights,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
