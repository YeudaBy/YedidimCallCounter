package com.yeudaby.callscounter.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.yeudaby.calls_counter.shared.domain.model.Region
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(id = R.string.settings_saved)

    LaunchedEffect(uiState.savedMessage) {
        if (uiState.savedMessage) {
            snackbarHostState.showSnackbar(savedMessage)
            viewModel.dismissSavedMessage()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(id = R.string.settings)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Personal Information
                Text(
                    text = stringResource(id = R.string.settings_personal_info),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(id = R.string.settings_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.dispatcherNumber,
                    onValueChange = viewModel::updateDispatcherNumber,
                    label = { Text(stringResource(id = R.string.settings_dispatcher_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                var regionExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = regionExpanded,
                    onExpandedChange = { regionExpanded = !regionExpanded }
                ) {
                    OutlinedTextField(
                        value = getRegionString(uiState.region),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.settings_region)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = regionExpanded,
                        onDismissRequest = { regionExpanded = false }
                    ) {
                        Region.entries.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(getRegionString(region)) },
                                onClick = {
                                    viewModel.updateRegion(region)
                                    regionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Goals
                Text(
                    text = stringResource(id = R.string.settings_goals),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(text = "${stringResource(id = R.string.settings_weekly_goal)}: ${uiState.weeklyGoal}")
                Slider(
                    value = uiState.weeklyGoal.toFloat(),
                    onValueChange = { viewModel.updateWeeklyGoal(it.toInt()) },
                    valueRange = 10f..200f,
                    steps = 18,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Privacy
                Text(
                    text = stringResource(id = R.string.settings_privacy),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                        Text(
                            text = stringResource(id = R.string.settings_anonymous),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.settings_anonymous_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isAnonymous,
                        onCheckedChange = viewModel::updateAnonymousMode
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = viewModel::saveSettings,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.settings_save))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun getRegionString(region: Region): String {
    return when (region) {
        Region.JERUSALEM -> stringResource(id = R.string.region_jerusalem)
        Region.NORTH -> stringResource(id = R.string.region_north)
        Region.SOUTH -> stringResource(id = R.string.region_south)
        Region.CENTER -> stringResource(id = R.string.region_center)
    }
}
