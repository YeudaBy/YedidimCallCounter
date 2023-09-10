package com.yeudaby.callscounter.screens.mainScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    navigateToInfo: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) { viewModel.init(context) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            viewModel.refresh(context)
            kotlinx.coroutines.delay(3_000)
        }
    }


    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) },
            actions = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = stringResource(R.string.info),
                    modifier = Modifier.clickable {
                        navigateToInfo()
                    }.padding(8.dp)
                )
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
    LazyColumn {

        items(items = viewModel.getStats().filter {
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
fun Calls(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn {
        items(items = uiState.filteredCalls) { callLogEntry ->
            CallLogEntryItem(callLogEntry = callLogEntry)
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filters(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onDurationCheckedChange(
                    isChecked = !uiState.withDuration,
                )
            }
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = uiState.withDuration, onClick = {
                viewModel.onDurationCheckedChange(
                    isChecked = !uiState.withDuration,
                )
            })
            Text(text = stringResource(R.string.only_from_15_seconds_duration))
        }

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


private fun Long.format(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()).format(date)
}