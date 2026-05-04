package com.yeudaby.callscounter.screens.settings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.screens.mainScreen.MainScreenViewModel
import com.yeudaby.callscounter.ui.theme.LocalAppColors

@Composable
fun SettingsTab() {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.factory(context))
    val filtersViewModel: MainScreenViewModel = viewModel()
    val weekGoal by settingsViewModel.weekGoal.collectAsState()
    val filtersState by filtersViewModel.uiState.collectAsState()
    val colors = LocalAppColors.current
    var goalInput by rememberSaveable(weekGoal) { mutableStateOf(weekGoal.toString()) }
    val parsedGoal = goalInput.toIntOrNull()?.coerceAtLeast(1)

    LaunchedEffect(Unit) {
        filtersViewModel.init(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.week_goal_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.text,
                )
                Text(
                    text = stringResource(R.string.week_goal_description),
                    fontSize = 13.sp,
                    color = colors.muted,
                )
            }
        }

        item {
            SettingsCard {
                Text(
                    text = stringResource(R.string.week_goal_current, weekGoal),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accent,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { updated -> goalInput = updated.filter(Char::isDigit) },
                        label = { Text(stringResource(R.string.week_goal_input_label)) },
                        shape = RoundedCornerShape(50.dp),
                        supportingText = {
                            Text(
                                text = if (goalInput.isNotEmpty() && parsedGoal == null) {
                                    stringResource(R.string.week_goal_invalid)
                                } else {
                                    stringResource(R.string.week_goal_input_hint)
                                }
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    IconButton(
                        {
                            parsedGoal?.let(settingsViewModel::setWeekGoal)
                        },
                        enabled = parsedGoal != null && parsedGoal != weekGoal,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            null,
                        )
                    }

                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf(40, 60, 100, 150, 200)) { preset ->
                        ElevatedButton(
                            onClick = {
                                goalInput = preset.toString()
                                settingsViewModel.setWeekGoal(preset)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(preset.toString())
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.text,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.filters_description),
                fontSize = 13.sp,
                color = colors.muted,
            )
        }

        item {
            SettingsCard {
                Text(
                    text = stringResource(R.string.minimum_duration_seconds),
                    fontSize = 12.sp,
                    color = colors.muted,
                )
                Slider(
                    value = filtersState.fromDuration.toFloat(),
                    onValueChange = { filtersViewModel.onDurationChange(it.toInt(), context) },
                    valueRange = 0f..300f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.accent,
                        activeTrackColor = colors.accent,
                        inactiveTrackColor = colors.accent.copy(alpha = 0.2f),
                    ),
                )
                Box(
                    modifier = Modifier
                        .background(colors.accent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "${filtersState.fromDuration}s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accent,
                    )
                }
            }
        }

        item {
            SettingsCard {
                Text(
                    text = stringResource(R.string.call_types),
                    fontSize = 12.sp,
                    color = colors.muted,
                )
                CallType.entries.forEach { callType ->
                    SettingsCallTypeRow(
                        callType = callType,
                        isSelected = filtersState.selectedCallTypes.contains(callType),
                        onClick = { filtersViewModel.onCallTypeCheckedChange(callType) },
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.accent.copy(alpha = 0.055f), RoundedCornerShape(16.dp))
                    .border(1.dp, colors.accent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.monitored_hotline),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accent,
                )
                Text("1230", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.text)
                Text(
                    "0533131310",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.text
                )
                Text(
                    text = stringResource(R.string.yedidim_hotline),
                    fontSize = 10.sp,
                    color = colors.muted,
                )
            }
        }

        item {
            SettingsCard {
                Text(
                    text = filtersState.filteredCalls.size.toString(),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.accent,
                )
                Text(
                    text = stringResource(R.string.total_calls_last_30_days),
                    fontSize = 12.sp,
                    color = colors.muted,
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun SettingsCallTypeRow(
    callType: CallType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    val color = callType.color

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) color.copy(alpha = 0.1f) else colors.surface,
                RoundedCornerShape(10.dp),
            )
            .border(
                1.dp,
                if (isSelected) color.copy(alpha = 0.33f) else colors.border,
                RoundedCornerShape(10.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (isSelected) color.copy(alpha = 0.2f) else colors.border,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(callType.iconRes),
                contentDescription = null,
                tint = if (isSelected) color else colors.muted,
                modifier = Modifier.size(15.dp),
            )
        }

        Text(
            text = stringResource(callType.titleRes),
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) colors.text else colors.muted,
        )

        Box(
            modifier = Modifier
                .size(20.dp)
                .background(if (isSelected) color else Color.Transparent, CircleShape)
                .border(2.dp, if (isSelected) color else colors.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
