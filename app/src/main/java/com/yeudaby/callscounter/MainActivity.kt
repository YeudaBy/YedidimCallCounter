package com.yeudaby.callscounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.yeudaby.callscounter.screens.InfoScreen
import com.yeudaby.callscounter.screens.counterScreen.CounterScreen
import com.yeudaby.callscounter.screens.mainScreen.MainScreen
import com.yeudaby.callscounter.screens.permissionScreen.PermissionScreen
import com.yeudaby.callscounter.ui.theme.CallsCounterTheme
import timber.log.Timber


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        FirebaseApp.initializeApp(this)

        setContent {
            CallsCounterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background,
                ) {
                    Navigation()
                }
            }
        }
    }

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "permissionScreen") {
            composable("permissionScreen") {
                AnimatedVisibility(
                    visible = true,
                    exit = fadeOut() + shrinkOut()
                ) {
                    PermissionScreen {
                        navController.navigate("home") {
                            popUpTo("permissionScreen") { inclusive = true }
                        }
                    }
                }
            }
            composable("home") {
                MainScreen {
                    navController.navigate("info")
                }
            }
            composable("counter") {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandIn(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    CounterScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            dialog("info") {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandIn(),
                    exit = fadeOut() + shrinkOut()
                ) {
                    InfoScreen {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}