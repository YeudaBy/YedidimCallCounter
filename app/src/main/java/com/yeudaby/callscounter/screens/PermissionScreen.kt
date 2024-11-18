package com.yeudaby.callscounter.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yeudaby.callscounter.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val callsLogPermissionsState = rememberPermissionState(
        permission = android.Manifest.permission.READ_CALL_LOG
    )

    val notificationPermissionState = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true -> rememberPermissionState(
            permission = android.Manifest.permission.POST_NOTIFICATIONS
        )

        else -> null
    }

    if (callsLogPermissionsState.status.isGranted && notificationPermissionState?.status?.isGranted == true) {
        onPermissionGranted()
    } else {
        PermissionScreenContent(
            callsLogPermissionsState = callsLogPermissionsState,
            notificationPermissionState = notificationPermissionState
        )
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreenContent(
    callsLogPermissionsState: PermissionState,
    notificationPermissionState: PermissionState?
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Text(text = "🙏")
                callsLogPermissionsState.status.isGranted.not().let {
                    Text(
                        text = stringResource(R.string.permission_request),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = {
                            callsLogPermissionsState.launchPermissionRequest()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.permission_button),
                        )
                    }
                }
                notificationPermissionState?.status?.isGranted?.not().let {
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = stringResource(R.string.notification_permission_request),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = {
                            notificationPermissionState?.launchPermissionRequest()
                        }
                    ) {
                        Text(text = stringResource(R.string.notification_permission_button))
                    }
                }
            }

        }
    }
}
