package io.github.subhamtyagi.ocr.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.subhamtyagi.ocr.data.BottomNavItems


@Composable
fun MyNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            selected = currentRoute == BottomNavItems.Home.route,
            onClick = {
                navController.navigate(BottomNavItems.Home.route)
            },
            icon = {
                Icon(
                    imageVector = BottomNavItems.Home.icon,
                    contentDescription = BottomNavItems.Home.label
                )
            },
            label = {
                Text(text = BottomNavItems.Home.label)
            }
        )
        NavigationBarItem(
            selected = currentRoute == BottomNavItems.Download.route,
            onClick = {
                navController.navigate(BottomNavItems.Download.route)
            },
            icon = {
                Icon(
                    imageVector = BottomNavItems.Download.icon,
                    contentDescription = BottomNavItems.Download.label
                )
            },
            label = {
                Text(text = BottomNavItems.Download.label)
            }
        )

        NavigationBarItem(
            selected = currentRoute == BottomNavItems.Settings.route,
            onClick = {
                navController.navigate(BottomNavItems.Settings.route)
            },
            icon = {
                Icon(
                    imageVector = BottomNavItems.Settings.icon,
                    contentDescription = BottomNavItems.Settings.label
                )
            },
            label = {
                Text(text = BottomNavItems.Settings.label)
            }
        )
    }
}

