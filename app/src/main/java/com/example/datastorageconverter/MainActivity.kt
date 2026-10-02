package com.example.datastorageconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.datastorageconverter.ui.components.AppBottomNavBar
import com.example.datastorageconverter.ui.navigation.Screen
import com.example.datastorageconverter.ui.screens.about.AboutScreen
import com.example.datastorageconverter.ui.screens.converter.ConverterScreen
import com.example.datastorageconverter.ui.screens.history.HistoryScreen
import com.example.datastorageconverter.ui.screens.settings.SettingsScreen
import com.example.datastorageconverter.ui.screens.splash.SplashScreen
import com.example.datastorageconverter.ui.theme.DataStorageConverterTheme
import com.example.datastorageconverter.ui.viewmodel.DataStorageConverterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val converterViewModel: DataStorageConverterViewModel = viewModel()
            val settings by converterViewModel.settings.collectAsState()

            DataStorageConverterTheme(
                appTheme = settings.theme,
                dynamicColor = settings.dynamicColor
            ) {
                MainAppScaffold(viewModel = converterViewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: DataStorageConverterViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null && currentRoute != Screen.Splash.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Converter.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Converter.route) {
                ConverterScreen(viewModel = viewModel)
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToConverter = {
                        navController.navigate(Screen.Converter.route) {
                            popUpTo(Screen.Converter.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}