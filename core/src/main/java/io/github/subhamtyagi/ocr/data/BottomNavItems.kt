package io.github.subhamtyagi.ocr.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItems(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    data object Home : BottomNavItems("Home", Icons.Filled.Home, "home")
    data object Settings : BottomNavItems("Settings", Icons.Filled.Settings, "settings")
    data object Download : BottomNavItems("Download", Icons.Filled.Favorite, "download")
}




