package io.github.subhamtyagi.ocr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.subhamtyagi.ocr.data.BottomNavItems
import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.ui.composables.MyNavigationBar
import io.github.subhamtyagi.ocr.ui.screens.DownloadLanguageDataScreen
import io.github.subhamtyagi.ocr.ui.screens.HomeScreen
import io.github.subhamtyagi.ocr.ui.screens.SettingsScreen
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(applicationContext)
        val viewModel = SettingsViewModel(dataStoreManager)
        enableEdgeToEdge()
        setContent {
            CharacherRecognizerTheme {
                MyApp(viewModel)
            }
        }
    }
}


@Composable
fun MyApp(viewModel: SettingsViewModel) {
    val navController = rememberNavController()
    Surface(color = Color.White) {
        Scaffold(
            bottomBar = {
                MyNavigationBar(navController = navController)
            }, content = { padding ->
                NavHost(
                    navController = navController,
                    startDestination = BottomNavItems.Home.route,
                    modifier = Modifier.padding(paddingValues = padding)
                ) {
                    composable(BottomNavItems.Home.route) {
                        HomeScreen(viewModel, navController, Modifier.fillMaxSize())
                    }
                    composable(BottomNavItems.Download.route) {
                        DownloadLanguageDataScreen(viewModel, navController, Modifier.fillMaxSize())
                    }
                    composable(BottomNavItems.Settings.route) {
                        SettingsScreen(viewModel, navController, Modifier.fillMaxSize())
                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
}


