package com.yeudaby.callscounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.yeudaby.callscounter.screens.InfoScreen
import com.yeudaby.callscounter.screens.mainScreen.CallsLogScreen
import com.yeudaby.callscounter.screens.mainScreen.SettingsScreen
import com.yeudaby.callscounter.screens.mainScreen.StatisticsScreen
import com.yeudaby.callscounter.screens.permission.PermissionScreen
import com.yeudaby.callscounter.ui.theme.CallsCounterTheme
import timber.log.Timber

enum class NavigationItem(
    val route: String,
) {
    Permissions("permissions"),
    Info("info"),
    Calls("calls"),
    Statistics("stats"),
    Settings("settings")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavItems = listOf(
                NavigationItem.Calls,
                NavigationItem.Statistics,
                NavigationItem.Settings,
            )
            val bottomNavTitles = listOf(R.string.calls, R.string.stats, R.string.settings)
            val bottomNavIcons = listOf(
                R.drawable.call_log_24px,
                R.drawable.leaderboard_24px,
                R.drawable.settings_24px,
            )
            val bottomNavSelectedIcons = listOf(
                R.drawable.call_log_24px__fill,
                R.drawable.leaderboard_24px__fill,
                R.drawable.settings_24px__fill,
            )

            val showBottomBar = currentRoute != null
                    && currentRoute != NavigationItem.Permissions.route

            CallsCounterTheme {
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                bottomNavItems.forEachIndexed { index, item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(NavigationItem.Calls.route)
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(
                                                    if (selected) bottomNavSelectedIcons[index]
                                                    else bottomNavIcons[index]
                                                ),
                                                contentDescription = stringResource(bottomNavTitles[index]),
                                            )
                                        },
                                        label = { Text(text = stringResource(bottomNavTitles[index])) },
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Navigation(navController, Modifier.padding(paddingValues))
                }
            }
        }
    }

    @Composable
    private fun Navigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Permissions.route,
            modifier = modifier,
        ) {
            composable(NavigationItem.Permissions.route) {
                PermissionScreen {
                    navController.navigate(NavigationItem.Calls.route) {
                        popUpTo(NavigationItem.Permissions.route) { inclusive = true }
                    }
                }
            }
            composable(NavigationItem.Calls.route) {
                CallsLogScreen(
                    navigateToInfo = { navController.navigate(NavigationItem.Info.route) },
                    navigateToSettings = { navController.navigate(NavigationItem.Settings.route) }
                )
            }
            composable(NavigationItem.Statistics.route) {
                StatisticsScreen(
                    navigateToInfo = { navController.navigate(NavigationItem.Info.route) },
                    navigateToSettings = { navController.navigate(NavigationItem.Settings.route) }
                )
            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen(
                    navigateToInfo = { navController.navigate(NavigationItem.Info.route) },
                    navigateToSettings = { navController.navigate(NavigationItem.Settings.route) }
                )
            }
            dialog(NavigationItem.Info.route) {
                InfoScreen {
                    navController.popBackStack()
                }
            }
        }
    }

}
