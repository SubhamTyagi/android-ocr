package io.github.subhamtyagi.ocr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.subhamtyagi.ocr.data.NavigationItems
import io.github.subhamtyagi.ocr.ui.composables.MyNavigationBar
import io.github.subhamtyagi.ocr.ui.screens.DownloadLanguageDataScreen
import io.github.subhamtyagi.ocr.ui.screens.HomeScreen
import io.github.subhamtyagi.ocr.ui.screens.SettingsImageProcessingScreen
import io.github.subhamtyagi.ocr.ui.screens.SettingsScreen
import io.github.subhamtyagi.ocr.ui.screens.SettingsTesseractParameter
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val viewModel: SettingsViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            CharacherRecognizerTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()
    Surface(color = Color.White) {
        Scaffold(
            bottomBar = {
                MyNavigationBar(navController = navController)
            }, content = { padding ->
                NavHost(
                    navController = navController,
                    startDestination = NavigationItems.Home.route,
                    modifier = Modifier.padding(paddingValues = padding)
                ) {
                    composable(NavigationItems.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(NavigationItems.Download.route) {
                        DownloadLanguageDataScreen(navController)
                    }
                    composable(NavigationItems.Settings.route) {
                        SettingsScreen(navController)
                    }
                    composable(NavigationItems.SettingsImageProcessing.route) {
                        SettingsImageProcessingScreen(navController)
                    }
                    composable(NavigationItems.SettingsTesseractParameter.route) {
                        SettingsTesseractParameter(navController)
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CharacherRecognizerTheme {
        MyApp()
    }
}


