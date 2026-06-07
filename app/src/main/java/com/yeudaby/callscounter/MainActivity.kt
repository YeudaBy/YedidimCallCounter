package com.yeudaby.callscounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.yeudaby.callscounter.ui.home.HomeScreen
import com.yeudaby.callscounter.ui.leaderboard.LeaderboardScreen
import com.yeudaby.callscounter.ui.permissions.PermissionsScreen
import com.yeudaby.callscounter.ui.settings.SettingsScreen
import com.yeudaby.callscounter.ui.theme.CallsCounterTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CallsCounterTheme {
                var permissionsGranted by remember { mutableStateOf(false) }

                if (!permissionsGranted) {
                    PermissionsScreen(
                        onPermissionsGranted = { permissionsGranted = true }
                    )
                } else {
                    val navController = rememberNavController()
                    
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Text(stringResource(R.string.home)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "leaderboard",
                                    onClick = {
                                        navController.navigate("leaderboard") {
                                            popUpTo("home")
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Text(stringResource(R.string.leaderboard)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "settings",
                                    onClick = {
                                        navController.navigate("settings") {
                                            popUpTo("home")
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") { HomeScreen() }
                            composable("leaderboard") { LeaderboardScreen() }
                            composable("settings") { SettingsScreen() }
                        }
                    }
                }
            }
        }
    }
}
