package io.github.subhamtyagi.ocr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.subhamtyagi.ocr.ui.NavigationItems
import io.github.subhamtyagi.ocr.ui.composables.BottomNavBar
import io.github.subhamtyagi.ocr.ui.screens.DownloadLanguageDataScreen
import io.github.subhamtyagi.ocr.ui.screens.HomeScreen
import io.github.subhamtyagi.ocr.ui.screens.ImageProcessingScreen
import io.github.subhamtyagi.ocr.ui.screens.SettingsScreen
import io.github.subhamtyagi.ocr.ui.screens.TesseractParametersScreen
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var sharedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            CharacherRecognizerTheme {
                MyApp(sharedImageUri = sharedImageUri, onSharedImageHandled = { sharedImageUri = null })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }
            uri?.let {
                sharedImageUri = it
            }
        }
    }
}

@Composable
fun MyApp(sharedImageUri: Uri? = null, onSharedImageHandled: () -> Unit = {}) {
    val navController = rememberNavController()
    Surface(color = Color.White) {
        Scaffold(bottomBar = {
            BottomNavBar(navController = navController)
        }, content = { padding ->
            NavHost(
                navController = navController,
                startDestination = NavigationItems.Home.route,
                modifier = Modifier.padding(paddingValues = padding)
            ) {
                composable(NavigationItems.Home.route) {
                    HomeScreen(
                        sharedImageUri = sharedImageUri,
                        onSharedImageHandled = onSharedImageHandled
                    )
                }
                composable(NavigationItems.Download.route) {
                    DownloadLanguageDataScreen()
                }
                composable(NavigationItems.Settings.route) {
                    SettingsScreen(navController = navController)
                }
                composable(NavigationItems.SettingsImageProcessing.route) {
                    ImageProcessingScreen()
                }
                composable(NavigationItems.SettingsTesseractParameter.route) {
                    TesseractParametersScreen()
                }
            }
        })
    }
}