package com.yeudaby.callscounter.screens.permission

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.exitApp
import com.yeudaby.callscounter.openAppSettings
import com.yeudaby.callscounter.ui.theme.InstrumentSerif
import timber.log.Timber

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = viewModel(),
    onPermissionGranted: () -> Unit
) {
    val currentPermission = viewModel.currentPermission.collectAsState().value
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { context.exitApp() },
    ) {

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
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForCallLogPermission(onGranted: () -> Unit) {
    val context = LocalContext.current
    val callLogPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.READ_CALL_LOG
    )

    Timber.w("AskForCallLogPermission")

    if (callLogPermissionState.status.isGranted) {
        onGranted()
    } else {
        PermissionRequestUI(
            description = stringResource(R.string.call_log_permission_request),
            buttonText = stringResource(R.string.call_log_permission_button),
            onRequestPermission = { callLogPermissionState.launchPermissionRequest() },
            secondaryButtonText = stringResource(R.string.open_settings_button),
            onSecondaryAction = { context.openAppSettings() }
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
    onSkip: (() -> Unit)? = null,
    secondaryButtonText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
) {
    Card {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .padding(vertical = 16.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = stringResource(R.string.welcome),
                    style = MaterialTheme.typography.headlineLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center,
                    fontFamily = InstrumentSerif
                )
            }
            Text(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.padding(6.dp))
            Button(onClick = onRequestPermission, Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text(text = buttonText)
            }
            if (secondaryButtonText != null && onSecondaryAction != null) {
                OutlinedButton(onClick = onSecondaryAction) {
                    Text(text = secondaryButtonText)
                }
            }
            if (allowSkip && onSkip != null) {
                OutlinedButton(onClick = onSkip) {
                    Text(text = stringResource(R.string.skip_text))
                }
            }
        }
    }
}
