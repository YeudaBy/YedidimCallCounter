package com.yeudaby.callscounter.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.repository.CallsRepository
import com.yeudaby.calls_counter.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val syncSuccess: Boolean = false
)

class LeaderboardViewModel(
    private val callsRepository: CallsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    fun syncDataAndFetchLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, error = null, syncSuccess = false)
            
            val userResult = userRepository.getUser()
            val user = if (userResult is Result.Success) userResult.data else null

            if (user?.isAnonymous == true) {
                // Skip syncing if user chose to be anonymous
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncSuccess = true,
                    error = "מצב אנונימי פעיל - הנתונים שלך לא משותפים."
                )
                return@launch
            }

            val fromTimestamp = Clock.System.now()
                .minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
            
            val records = callsRepository.getCallRecordsFlow(fromTimestamp).first()

            val result = callsRepository.syncCallRecords(records)
            
            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isSyncing = false, syncSuccess = true)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false, 
                        error = "שגיאת רשת: לא ניתן להסתנכרן מול השרת ללא חיבור לאינטרנט."
                    )
                }
            }
        }
    }
}
