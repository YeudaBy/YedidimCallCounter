package com.yeudaby.callscounter.screens.mainScreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import com.yeudaby.callscounter.screens.rememberMarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    navigateToInfo: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        while (true) {
            viewModel.init(context)
            kotlinx.coroutines.delay(3_000)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) },
            navigationIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.horizontal_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .clickable {
                            navigateToInfo()
                        }
                        .padding(4.dp)
                        .size(62.dp)
                )
            },
            actions = {
                IconButton(onClick = { viewModel.shareApp(context) }) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = stringResource(R.string.share),
                    )
                }
                IconButton(onClick = { navigateToInfo() }) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = stringResource(R.string.info),
                    )
                }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                AnimatedContent(targetState = uiState.filteredCalls.size, label = "") { count ->
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                Text(text = stringResource(R.string.calls))
            }
        }

        Tabs(
            viewModel = viewModel,
        )

    }

}

@Composable
fun Stats(
    viewModel: MainScreenViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun shareImage(imageBitmap: ImageBitmap) {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val shareImageFile = File(cachePath, "image.jpg")

        try {
            val stream = FileOutputStream(shareImageFile)
            imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                shareImageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn {

        item {
            CallsPerDay(
                data = uiState.filteredCalls,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(items = uiState.data.filter {
            it.count > 0
        }.chunked(2)) { stats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                stats.forEach { stat ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .clickable {
                                coroutineScope.launch {
                                    shareImage(stat.toImageBitmap(context))
                                }
                            }
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = stat.color.copy(alpha = 0.1f),
                            contentColor = Color.White,
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            Arrangement.Center,
                            Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = stat.label),
                                textAlign = TextAlign.Center,
                                color = stat.color
                            )
                            Text(
                                text = "(${stat.fromMillis.formatLight()})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = stat.count.toString(),
                                style = MaterialTheme.typography.displaySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CallsPerDay(
    data: List<CallLogEntry>,
    modifier: Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val marker = rememberMarker()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.tryRunTransaction {
                val days = data.map { it.date.getDay() }.distinct()
                lineSeries {
                    series(
                        days,
                        days.map { day ->
                            data.filter { it.date.getDay() == day }.size
                        }
                    )

                }
            }
        }
    }

    Column {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    listOf(
                        rememberLineSpec(
                            DynamicShader.color(
                                Color(
                                    0xffa485e0
                                )
                            )
                        )
                    )
                ),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    guideline = null,
                    valueFormatter = { value, chartValues, verticalAxisPosition ->
                        value.toInt().getDayOfMonth()
                    },
                    labelRotationDegrees = 12f,
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onSurface,
                        textSize = 12.sp,
                    ),
                ),
            ),
            modelProducer = modelProducer,
            modifier = modifier,
            marker = marker,
            zoomState = rememberVicoZoomState(zoomEnabled = false),
            placeholder = {
                Text(
                    text = "No data",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            },
        )
    }
}

private fun Long.getDay(): Int {
    // return the day part of the date
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar.get(Calendar.DAY_OF_YEAR)
}

private fun Int.getDayOfMonth(): String {
    // return the day part of the date
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_YEAR, this)
    return SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calls(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn {
        uiState.filteredCalls.groupBy { it.date.getDay() }.forEach { (day, calls) ->
            stickyHeader {
                Text(
                    text = day.getDayOfMonth(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xff000000),
                                    Color(0x00000000),
                                ),
                                start = Offset.Infinite,
                                end = Offset.Zero
                            )
                        )
                        .padding(12.dp)
                )
            }
            items(items = calls) { callLogEntry ->
                CallLogEntryItem(callLogEntry = callLogEntry)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun Filters(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = if (uiState.fromDuration == 0) "" else uiState.fromDuration.toString(),
            onValueChange = { viewModel.onDurationChange(it.toIntOrNull() ?: 0, context) },
            label = { Text(text = stringResource(R.string.from_duration)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Go
            ),
            supportingText = {
                Text(text = stringResource(R.string.seconds_helper_text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(R.string.call_types))

            CallType.values().forEach {
                FilterChip(
                    selected = uiState.selectedCallTypes.contains(it),
                    onClick = { viewModel.onCallTypeCheckedChange(it) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = stringResource(id = it.titleRes)
                        )
                    },
                    label = { Text(text = stringResource(id = it.titleRes)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = it.color.copy(alpha = 0.1f),
                        selectedLeadingIconColor = it.color,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}


data class TabItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screen: @Composable () -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    viewModel: MainScreenViewModel
) {

    val tabs = listOf(
        TabItem(
            title = R.string.calls,
            icon = R.drawable.baseline_checklist_24,
            screen = { Calls(viewModel = viewModel) },
        ), TabItem(
            title = R.string.stats,
            icon = R.drawable.baseline_bar_chart_24,
            screen = { Stats(viewModel = viewModel) },
        ), TabItem(
            title = R.string.filters,
            icon = R.drawable.baseline_filter_list_24,
            screen = { Filters(viewModel = viewModel) },
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size },
    )
    val coroutineScope = rememberCoroutineScope()
    TabRow(selectedTabIndex = pagerState.currentPage) {
        tabs.forEachIndexed { index, tabItem ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = stringResource(id = tabItem.title)) },
                icon = {
                    Icon(
                        painter = painterResource(id = tabItem.icon),
                        contentDescription = stringResource(id = tabItem.title),
                    )
                },
            )
        }
    }
    HorizontalPager(state = pagerState) { page ->
        tabs[page].screen()
    }
}

@Composable
fun CallLogEntryItem(callLogEntry: CallLogEntry) {
    ListItem(
        headlineContent = { Text(text = callLogEntry.number) },
        supportingContent = {
            Text(
                text = stringResource(
                    id = R.string.duration,
                    callLogEntry.duration.toDurationString()
                )
            )
        },
        overlineContent = { Text(text = callLogEntry.date.format()) },
        leadingContent = {
            Icon(
                painter = painterResource(id = callLogEntry.type.iconRes),
                contentDescription = callLogEntry.type.toString()
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = callLogEntry.type.color.copy(alpha = 0.1f),
            leadingIconColor = callLogEntry.type.color,
            supportingColor = Color.White.copy(alpha = 0.5f),
        ),
    )
}

fun Long.toDurationString(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.toDurationText(context: Context): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    val hoursString = context.resources.getQuantityString(R.plurals.hours, hours.toInt(), hours)
    val minutesString =
        context.resources.getQuantityString(R.plurals.minutes, minutes.toInt(), minutes)
    val secondsString =
        context.resources.getQuantityString(R.plurals.seconds, seconds.toInt(), seconds)
    // build the string and omit empty parts
    return StringBuilder().apply {
        if (hours > 0) {
            append(hoursString)
        }
        if (minutes > 0) {
            if (isNotEmpty()) {
                append(" ")
            }
            append(minutesString)
        }
        if (seconds > 0) {
            if (isNotEmpty()) {
                append(" & ")
            }
            append(secondsString)
        }
    }.toString()
}


private fun Long.format(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()).format(date)
}

private fun Long.formatLight(): String {
    val date = Date(this)
    return SimpleDateFormat("MM/dd, HH:mm", Locale.getDefault()).format(date)
}