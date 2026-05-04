package com.yeudaby.callscounter.screens.mainScreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.screens.settings.SettingsTab
import com.yeudaby.callscounter.ui.theme.AccentPurple
import com.yeudaby.callscounter.ui.theme.InstrumentSerif
import com.yeudaby.callscounter.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun CallsLogScreen(
    viewModel: MainScreenViewModel = viewModel(),
    navigateToInfo: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.init(context) }

    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        AppHeader(
            todayCount    = uiState.todayCount,
            onShareClick  = { viewModel.shareApp(context) },
            onInfoClick   = navigateToInfo,
        )

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colors.accent,
                trackColor = colors.accent.copy(alpha = 0.15f),
            )
        }

        WeekProgressBanner(
            weekCount = uiState.weekCount,
            weekGoal  = uiState.weekGoal,
            onShareClick = { viewModel.shareWeekProgress(context) },
            navigateToSettings
        )

        Box(modifier = Modifier.weight(1f)) {
            CallsTab(viewModel)
        }
    }
}

@Composable
fun StatisticsScreen(
    viewModel: MainScreenViewModel = viewModel(),
    navigateToInfo: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.init(context) }

    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        AppHeader(
            todayCount = uiState.todayCount,
            onShareClick = { viewModel.shareApp(context) },
            onInfoClick = navigateToInfo,
        )

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colors.accent,
                trackColor = colors.accent.copy(alpha = 0.15f),
            )
        }

        WeekProgressBanner(
            weekCount = uiState.weekCount,
            weekGoal = uiState.weekGoal,
            onShareClick = { viewModel.shareWeekProgress(context) },
            navigateToSettings
        )

        Box(modifier = Modifier.weight(1f)) {
            StatsTab(viewModel)
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: MainScreenViewModel = viewModel(),
    navigateToInfo: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.init(context) }

    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        AppHeader(
            todayCount = uiState.todayCount,
            onShareClick = { viewModel.shareApp(context) },
            onInfoClick = navigateToInfo,
        )

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colors.accent,
                trackColor = colors.accent.copy(alpha = 0.15f),
            )
        }

        WeekProgressBanner(
            weekCount = uiState.weekCount,
            weekGoal = uiState.weekGoal,
            onShareClick = { viewModel.shareWeekProgress(context) },
            navigateToSettings
        )

        Box(modifier = Modifier.weight(1f)) {
            SettingsTab()
        }
    }
}



// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun AppHeader(
    todayCount: Int,
    onShareClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind {
                drawLine(
                    color = colors.border,
                    start = Offset(0f, size.height),
                    end   = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Logo box
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.accent.copy(alpha = 0.15f))
                .border(1.5.dp, colors.accent.copy(alpha = 0.33f), RoundedCornerShape(12.dp))
                .padding(7.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter           = painterResource(R.drawable.horizontal_logo),
                contentDescription = null,
                tint              = colors.accent,
                modifier          = Modifier.fillMaxSize(),
            )
        }

        // App name + org
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = stringResource(R.string.app_name),
                fontFamily = InstrumentSerif,
                fontStyle  = FontStyle.Italic,
                fontSize   = 18.sp,
                color      = colors.text,
                lineHeight = 20.sp,
            )
            Text(
                text     = "Yedidim",
                fontSize = 10.sp,
                color    = colors.muted,
            )
        }

        // Today count
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text       = todayCount.toString(),
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = colors.accent,
                lineHeight = 28.sp,
            )
            Text(
                text          = "TODAY",
                fontSize      = 9.sp,
                color         = colors.muted,
                letterSpacing = 0.8.sp,
            )
        }

        // Action icons
        Column(verticalArrangement = Arrangement.spacedBy((-4).dp)) {
            IconButton(onClick = onInfoClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector       = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.info),
                    tint              = colors.muted,
                    modifier          = Modifier.size(18.dp),
                )
            }
            IconButton(onClick = onShareClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector       = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share),
                    tint              = colors.muted,
                    modifier          = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ─── Week progress ────────────────────────────────────────────────────────────

@Composable
private fun WeekProgressBanner(
    weekCount: Int,
    weekGoal: Int,
    onShareClick: () -> Unit,
    onSetClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val safeWeekGoal = weekGoal.coerceAtLeast(1)
    val pct = (weekCount.toFloat() / safeWeekGoal.toFloat()).coerceIn(0f, 1f)
    val animatedPct by animateFloatAsState(targetValue = pct, animationSpec = tween(600), label = "weekPct")
    val goalReached = pct >= 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.accent.copy(alpha = 0.055f))
            .drawBehind {
                drawLine(
                    color       = colors.border,
                    start       = Offset(0f, size.height),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.weekly_goal_progress),
                fontSize = 11.sp,
                color = colors.muted,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text       = "$weekCount / $safeWeekGoal",
                    fontSize   = 11.sp,
                    color      = colors.accent,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_week_progress),
                        tint = colors.muted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.accent.copy(alpha = 0.15f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPct)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (goalReached) colors.incoming
                        else colors.accent
                    ),
            )
        }

        if (goalReached) {
            Text(
                text       = stringResource(R.string.goal_reached),
                fontSize   = 10.sp,
                color      = colors.incoming,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─── Calls tab ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CallsTab(viewModel: MainScreenViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val colors  = LocalAppColors.current
    val groupedCalls = uiState.filteredCalls.groupBy { startOfDay(it.date) }

    if (uiState.filteredCalls.isEmpty() && !uiState.isLoading) {
        EmptyState(
            message = uiState.callLogHint?.let { stringResource(it) }
                ?: stringResource(R.string.no_calls_match_filters)
        )
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        groupedCalls.forEach { (_, calls) ->
            stickyHeader {
                val day = calls.first().date
                Text(
                    text     = day.formatDate(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color    = colors.muted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface.copy(alpha = 0.92f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
            items(calls) { entry ->
                CallRow(entry)
            }
        }
    }
}

@Composable
fun CallRow(call: CallLogEntry) {
    val colors = LocalAppColors.current
    val typeColor = when (call.type) {
        CallType.INCOMING -> colors.incoming
        CallType.OUTGOING -> colors.outgoing
        CallType.MISSED   -> colors.missed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(typeColor.copy(alpha = 0.04f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment  = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(typeColor.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter           = painterResource(call.type.iconRes),
                contentDescription = null,
                tint              = typeColor,
                modifier          = Modifier.size(17.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = call.number,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                color      = colors.text,
            )
            Text(
                text     = "${call.date.formatDate()} · ${call.date.formatTime()}",
                fontSize = 11.sp,
                color    = colors.muted,
            )
        }

        Text(
            text       = if (call.type == CallType.MISSED) "Missed" else call.duration.toDurationString(),
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = typeColor,
        )
    }
    HorizontalDivider(color = colors.border.copy(alpha = 0.5f))
}

// ─── Stats tab ────────────────────────────────────────────────────────────────

@Composable
private fun StatsTab(viewModel: MainScreenViewModel) {
    val uiState       = viewModel.uiState.collectAsState().value
    val colors        = LocalAppColors.current
    val context       = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (uiState.data.all { it.count == 0 } && !uiState.isLoading) {
        EmptyState(stringResource(R.string.no_statistics_available))
        return
    }

    val rings = listOf(
        RingData(stringResource(R.string.last_hour),  "",  uiState.data.firstOrNull { it.label == R.string.start_of_hour  }?.count ?: 0, colors.outgoing),
        RingData(stringResource(R.string.today),  "",   uiState.todayCount,  colors.accent),
        RingData(stringResource(R.string.this_week),  "",   uiState.weekCount,   AccentPurple),
        RingData(stringResource(R.string.this_month),  "",  uiState.data.firstOrNull { it.label == R.string.start_of_month }?.count ?: 0, colors.amber),
    )

    LazyColumn(
        modifier         = Modifier.fillMaxSize(),
        contentPadding   = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Ring progress row
        item {
            SurfaceCard {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    rings.forEach { ring ->
                        RingProgress(
                            value = ring.value,
                            max   = maxOf((ring.value * 1.5f).toInt(), 10),
                            color = ring.color,
                            label = ring.label,
                            sub   = ring.sub,
                        )
                    }
                }
            }
        }

        // 7-day bar chart
        item {
            SurfaceCard {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text       = "Last 7 days",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = colors.text,
                    )
                    MiniBarChart(calls = uiState.filteredCalls)
                }
            }
        }

        // Stat cards grid
        items(uiState.data.chunked(2)) { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { stat ->
                    StatCard(
                        label    = stringResource(stat.label),
                        sublabel = stat.fromMillis.formatLight(),
                        count    = stat.count,
                        color    = stat.color,
                        modifier = Modifier.weight(1f),
                        onClick  = {
                            coroutineScope.launch {
                                shareStatImage(stat.toImageBitmap(context), context)
                            }
                        },
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item {
            Text(
                text      = "Tap a card to share",
                fontSize  = 10.sp,
                color     = colors.muted,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class RingData(val label: String, val sub: String, val value: Int, val color: Color)

@Composable
private fun RingProgress(
    value: Int,
    max: Int,
    color: Color,
    label: String,
    sub: String,
    size: Dp = 60.dp,
) {
    val pct         = (value.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val animatedPct by animateFloatAsState(targetValue = pct, animationSpec = tween(700), label = "ring")
    val colors      = LocalAppColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier         = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke  = 5.dp.toPx()
                val inset   = stroke / 2f
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                val topLeft = Offset(inset, inset)

                drawArc(
                    color      = color.copy(alpha = 0.13f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color      = color,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedPct,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
            Text(
                text       = value.toString(),
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = colors.text,
            )
        }
        Text(text = label, fontSize = 10.sp, color = colors.muted, textAlign = TextAlign.Center)
        Text(text = sub, fontSize = 9.sp, color = color.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun MiniBarChart(calls: List<CallLogEntry>) {
    val colors = LocalAppColors.current
    val days = (6 downTo 0).map { daysBack ->
        val dayStart = startOfDay(System.currentTimeMillis() - daysBack * 86_400_000L)
        val dayEnd   = dayStart + 86_400_000L
        val count    = calls.count { it.date in dayStart until dayEnd && it.type == CallType.INCOMING }
        val label    = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayStart))
        Pair(label, count)
    }
    val max = days.maxOf { it.second }.coerceAtLeast(1)

    Column {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .height(72.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.Bottom,
        ) {
            days.forEachIndexed { index, (_, count) ->
                val isToday   = index == 6
                val barHeight = (count.toFloat() / max * 64).coerceAtLeast(4f).dp
                Column(
                    modifier              = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment   = Alignment.CenterHorizontally,
                    verticalArrangement   = Arrangement.Bottom,
                ) {
                    if (count > 0) {
                        Text(text = count.toString(), fontSize = 9.sp, color = colors.muted)
                    }
                    Spacer(Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (isToday) colors.accent else colors.accent.copy(alpha = 0.33f)
                            ),
                    )
                }
            }
        }

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            days.forEachIndexed { index, (label, _) ->
                val isToday = index == 6
                Text(
                    text       = label,
                    modifier   = Modifier.weight(1f),
                    textAlign  = TextAlign.Center,
                    fontSize   = 9.sp,
                    fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isToday) colors.accent else colors.muted,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    sublabel: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.07f))
            .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text          = label.uppercase(),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
            color         = color,
        )
        Text(text = sublabel, fontSize = 10.sp, color = colors.muted)
        Text(
            text       = count.toString(),
            fontSize   = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = colors.text,
            lineHeight = 36.sp,
        )
    }
}

// ─── Filters tab ─────────────────────────────────────────────────────────────

@Composable
private fun FiltersTab(viewModel: MainScreenViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val colors  = LocalAppColors.current

    LazyColumn(
        modifier         = Modifier.fillMaxSize(),
        contentPadding   = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.filters),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.text,
            )
        }

        // Duration slider
        item {
            SurfaceCard {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text     = "Minimum duration (seconds)",
                        fontSize = 12.sp,
                        color    = colors.muted,
                    )
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Slider(
                            value         = uiState.fromDuration.toFloat(),
                            onValueChange = { viewModel.onDurationChange(it.toInt(), context) },
                            valueRange    = 0f..300f,
                            modifier      = Modifier.weight(1f),
                            colors        = SliderDefaults.colors(
                                thumbColor        = colors.accent,
                                activeTrackColor  = colors.accent,
                                inactiveTrackColor = colors.accent.copy(alpha = 0.2f),
                            ),
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.accent.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text       = "${uiState.fromDuration}s",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = colors.accent,
                            )
                        }
                    }
                }
            }
        }

        // Call type toggles
        item {
            SurfaceCard {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text     = stringResource(R.string.call_types),
                        fontSize = 12.sp,
                        color    = colors.muted,
                    )
                    CallType.entries.forEach { callType ->
                        CallTypeToggleRow(
                            callType   = callType,
                            isSelected = uiState.selectedCallTypes.contains(callType),
                            onClick    = { viewModel.onCallTypeCheckedChange(callType) },
                        )
                    }
                }
            }
        }

        // Monitored hotlines info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.accent.copy(alpha = 0.055f))
                    .border(1.dp, colors.accent.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text       = stringResource(R.string.monitored_hotline).uppercase(),
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                    color      = colors.accent,
                )
                Text("📞 1230",        fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.text)
                Text("📞 0533131310",  fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.text)
                Text(
                    text     = stringResource(R.string.yedidim_hotline),
                    fontSize = 10.sp,
                    color    = colors.muted,
                )
            }
        }

        // Total count card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = uiState.filteredCalls.size.toString(),
                        fontSize   = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = colors.accent,
                        lineHeight = 48.sp,
                    )
                    Text(
                        text     = stringResource(R.string.total_matching_calls),
                        fontSize = 12.sp,
                        color    = colors.muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun CallTypeToggleRow(
    callType: CallType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    val color = when (callType) {
        CallType.INCOMING -> colors.incoming
        CallType.OUTGOING -> colors.outgoing
        CallType.MISSED   -> colors.missed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) color.copy(alpha = 0.1f) else colors.surface2)
            .border(1.dp, if (isSelected) color.copy(alpha = 0.33f) else colors.border, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isSelected) color.copy(alpha = 0.2f) else colors.border),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter           = painterResource(callType.iconRes),
                contentDescription = null,
                tint              = if (isSelected) color else colors.muted,
                modifier          = Modifier.size(15.dp),
            )
        }

        Text(
            text       = stringResource(callType.titleRes),
            modifier   = Modifier.weight(1f),
            fontSize   = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color      = if (isSelected) colors.text else colors.muted,
        )

        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else Color.Transparent)
                .border(2.dp, if (isSelected) color else colors.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Icon(
                    imageVector       = Icons.Filled.Check,
                    contentDescription = null,
                    tint              = Color.White,
                    modifier          = Modifier.size(12.dp),
                )
            }
        }
    }
}

// ─── Shared UI helpers ────────────────────────────────────────────────────────

@Composable
private fun SurfaceCard(content: @Composable () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

@Composable
private fun EmptyState(message: String) {
    val colors = LocalAppColors.current
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = message,
            fontSize  = 14.sp,
            color     = colors.muted,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(32.dp),
        )
    }
}

// ─── Share helper ─────────────────────────────────────────────────────────────

private fun shareStatImage(imageBitmap: ImageBitmap, context: Context) {
    val cachePath     = File(context.cacheDir, "images").also { it.mkdirs() }
    val shareImageFile = File(cachePath, "image.jpg")
    try {
        FileOutputStream(shareImageFile).use { stream ->
            imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", shareImageFile,
        )
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                },
                null,
            )
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// ─── Date/time utilities ──────────────────────────────────────────────────────

private fun startOfDay(ts: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ts
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun Long.dayOfYear(): Int = Calendar.getInstance().also { it.timeInMillis = this }.get(Calendar.DAY_OF_YEAR)

private fun Long.formatDate(): String =
    SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(this))

private fun Long.formatTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))

private fun Long.formatLight(): String =
    SimpleDateFormat("MM/dd, HH:mm", Locale.getDefault()).format(Date(this))

fun Long.toDurationString(): String {
    val h = this / 3600; val m = (this % 3600) / 60; val s = this % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}

fun Long.toDurationText(context: Context): String {
    val hours   = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return buildString {
        if (hours   > 0) append(context.resources.getQuantityString(R.plurals.hours,   hours.toInt(),   hours))
        if (minutes > 0) { if (isNotEmpty()) append(" "); append(context.resources.getQuantityString(R.plurals.minutes, minutes.toInt(), minutes)) }
        if (seconds > 0) { if (isNotEmpty()) append(" & "); append(context.resources.getQuantityString(R.plurals.seconds, seconds.toInt(), seconds)) }
    }
}
