package com.yeudaby.callscounter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.model.Region
import com.yeudaby.calls_counter.shared.domain.model.Role
import com.yeudaby.calls_counter.shared.domain.model.User
import com.yeudaby.calls_counter.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class SettingsUiState(
    val name: String = "",
    val dispatcherNumber: String = "",
    val region: Region = Region.CENTER,
    val weeklyGoal: Int = 40,
    val isAnonymous: Boolean = false,
    val isLoading: Boolean = true,
    val savedMessage: Boolean = false
)

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var currentUser: User? = null

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val result = userRepository.getUser()
            if (result is Result.Success) {
                currentUser = result.data
                _uiState.value = _uiState.value.copy(
                    name = result.data.name,
                    dispatcherNumber = result.data.dispatcherNumber,
                    region = result.data.region,
                    weeklyGoal = result.data.weeklyGoal,
                    isAnonymous = result.data.isAnonymous,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateDispatcherNumber(number: String) {
        _uiState.value = _uiState.value.copy(dispatcherNumber = number)
    }

    fun updateRegion(region: Region) {
        _uiState.value = _uiState.value.copy(region = region)
    }

    fun updateWeeklyGoal(goal: Int) {
        _uiState.value = _uiState.value.copy(weeklyGoal = goal)
    }

    fun updateAnonymousMode(isAnonymous: Boolean) {
        _uiState.value = _uiState.value.copy(isAnonymous = isAnonymous)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            val userToSave = currentUser?.copy(
                name = state.name,
                dispatcherNumber = state.dispatcherNumber,
                region = state.region,
                weeklyGoal = state.weeklyGoal,
                isAnonymous = state.isAnonymous
            ) ?: User(
                id = UUID.randomUUID().toString(),
                name = state.name,
                region = state.region,
                dispatcherNumber = state.dispatcherNumber,
                role = Role.REGULAR,
                fcmToken = null,
                weeklyGoal = state.weeklyGoal,
                isAnonymous = state.isAnonymous
            )
            
            userRepository.saveUser(userToSave)
            currentUser = userToSave
            
            _uiState.value = _uiState.value.copy(savedMessage = true)
        }
    }
    
    fun dismissSavedMessage() {
        _uiState.value = _uiState.value.copy(savedMessage = false)
    }
}
