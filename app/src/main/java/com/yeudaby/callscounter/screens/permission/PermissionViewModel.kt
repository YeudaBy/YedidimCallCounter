package com.yeudaby.callscounter.screens.permission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class Permission {
    CALL_LOG,
    NOTIFICATION,
    ALL_GRANTED
}

class PermissionViewModel : ViewModel() {

    private val _currentPermission = MutableStateFlow(Permission.CALL_LOG)
    val currentPermission: StateFlow<Permission> = _currentPermission

    fun updatePermissionState(isCallLogGranted: Boolean, isNotificationGranted: Boolean) {
        _currentPermission.update {
            when {
                isCallLogGranted && isNotificationGranted -> Permission.ALL_GRANTED
                !isCallLogGranted -> Permission.CALL_LOG
                else -> Permission.NOTIFICATION
            }
        }
    }
}
