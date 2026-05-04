package com.yeudaby.callscounter.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yeudaby.callscounter.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AppSettingsRepository,
) : ViewModel() {

    val weekGoal = repository.observeWeekGoal().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettingsRepository.DEFAULT_WEEK_GOAL,
    )

    fun setWeekGoal(goal: Int) {
        viewModelScope.launch {
            repository.setWeekGoal(goal)
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(
                        repository = AppSettingsRepository.getInstance(appContext),
                    ) as T
                }
            }
        }
    }
}
