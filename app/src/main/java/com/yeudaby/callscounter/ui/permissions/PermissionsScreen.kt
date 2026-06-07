package com.yeudaby.callscounter.ui.permissions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.yeudaby.callscounter.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    onPermissionsGranted: () -> Unit
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    )
    
    val callLogPermission = permissionsState.permissions.find { it.permission == android.Manifest.permission.READ_CALL_LOG }
    val callLogGranted = callLogPermission?.status?.isGranted == true

    if (callLogGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = stringResource(id = R.string.permissions_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = R.string.permissions_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        PermissionCard(
            title = stringResource(id = R.string.permission_call_log_title),
            description = stringResource(id = R.string.permission_call_log_desc),
            icon = Icons.Default.Info
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PermissionCard(
            title = stringResource(id = R.string.permission_phone_title),
            description = stringResource(id = R.string.permission_phone_desc),
            icon = Icons.Default.Call
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PermissionCard(
            title = stringResource(id = R.string.permission_notifications_title),
            description = stringResource(id = R.string.permission_notifications_desc),
            icon = Icons.Default.Notifications
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        // Basic check if the permission was completely denied and user didn't grant.
        // On newer Androids, shouldShowRationale is false if permanently denied.
        val isPermanentlyDenied = callLogPermission?.status is PermissionStatus.Denied &&
            !(callLogPermission.status as PermissionStatus.Denied).shouldShowRationale &&
            permissionsState.revokedPermissions.isNotEmpty()

        if (isPermanentlyDenied) {
            Text(
                text = stringResource(id = R.string.permissions_denied_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { permissionsState.launchMultiplePermissionRequest() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(id = R.string.btn_grant_permissions),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PermissionCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
