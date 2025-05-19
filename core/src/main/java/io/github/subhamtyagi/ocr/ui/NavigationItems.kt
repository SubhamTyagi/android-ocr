package io.github.subhamtyagi.ocr.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItems(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    data object Home : NavigationItems("Home", Icons.Filled.Home, "home")
    data object Settings : NavigationItems("Settings", Icons.Filled.Settings, "settings")
    data object Download : NavigationItems("Download", Icons.Filled.Download, "download")

    data object SettingsImageProcessing :
        NavigationItems("Settings", Icons.Filled.Settings, "settings_image_processing")

    data object SettingsTesseractParameter :
        NavigationItems("Settings", Icons.Filled.Settings, "settings_tesseract_parameter")
}