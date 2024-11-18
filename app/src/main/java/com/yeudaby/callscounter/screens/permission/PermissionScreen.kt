package com.yeudaby.callscounter.screens.permission

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yeudaby.callscounter.R
import timber.log.Timber

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onPermissionGranted: () -> Unit
) {
    val currentPermission = viewModel.currentPermission.collectAsState().value

    when (currentPermission) {
        Permission.CALL_LOG -> AskForCallLogPermission(
            onGranted = {
                viewModel.updatePermissionState(
                    isCallLogGranted = true,
                    isNotificationGranted = false
                )
            }
        )
        Permission.NOTIFICATION -> AskForNotificationPermission(
            onGranted = {
                viewModel.updatePermissionState(
                    isCallLogGranted = true,
                    isNotificationGranted = true
                )
            }
        )
        Permission.ALL_GRANTED -> onPermissionGranted()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForCallLogPermission(onGranted: () -> Unit) {
    val callLogPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.READ_CALL_LOG
    )

    Timber.w("AskForCallLogPermission")

    if (callLogPermissionState.status.isGranted) {
        onGranted()
    } else {
        PermissionRequestUI(
            description = stringResource(R.string.permission_request),
            buttonText = stringResource(R.string.permission_button),
            onRequestPermission = { callLogPermissionState.launchPermissionRequest() }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForNotificationPermission(onGranted: () -> Unit) {
    val notificationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )

    Timber.w("AskForNotificationPermission")

    if (notificationPermissionState.status.isGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        onGranted()
    } else {
        PermissionRequestUI(
            description = stringResource(R.string.notification_permission_request),
            buttonText = stringResource(R.string.notification_permission_button),
            onRequestPermission = { notificationPermissionState.launchPermissionRequest() },
            allowSkip = true,
            onSkip = onGranted
        )
    }
}

@Composable
fun PermissionRequestUI(
    description: String,
    buttonText: String,
    onRequestPermission: () -> Unit,
    allowSkip: Boolean = false,
    onSkip: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = onRequestPermission) {
            Text(text = buttonText)
        }
        if (allowSkip && onSkip != null) {
            OutlinedButton(onClick = onSkip) {
                Text(text = stringResource(R.string.skip_text))
            }
        }
    }
}
