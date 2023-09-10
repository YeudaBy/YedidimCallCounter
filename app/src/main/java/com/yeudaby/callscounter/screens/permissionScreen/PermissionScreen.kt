package com.yeudaby.callscounter.screens.permissionScreen

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

    if (callsLogPermissionsState.status.isGranted) {
        onPermissionGranted()
    } else {
        PermissionScreenContent(
            callsLogPermissionsState = callsLogPermissionsState,
        )
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreenContent(
    callsLogPermissionsState: PermissionState
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

        }
    }
}
