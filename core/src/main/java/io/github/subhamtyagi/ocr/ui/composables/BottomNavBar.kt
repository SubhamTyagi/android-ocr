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
import io.github.subhamtyagi.ocr.data.NavigationItems


@Composable
fun MyNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            selected = currentRoute == NavigationItems.Home.route,
            onClick = {
                navController.navigate(NavigationItems.Home.route)
            },
            icon = {
                Icon(
                    imageVector = NavigationItems.Home.icon,
                    contentDescription = NavigationItems.Home.label
                )
            },
            label = {
                Text(text = NavigationItems.Home.label)
            }
        )
        NavigationBarItem(
            selected = currentRoute == NavigationItems.Download.route,
            onClick = {
                navController.navigate(NavigationItems.Download.route)
            },
            icon = {
                Icon(
                    imageVector = NavigationItems.Download.icon,
                    contentDescription = NavigationItems.Download.label
                )
            },
            label = {
                Text(text = NavigationItems.Download.label)
            }
        )

        NavigationBarItem(
            selected = currentRoute == NavigationItems.Settings.route,
            onClick = {
                navController.navigate(NavigationItems.Settings.route)
            },
            icon = {
                Icon(
                    imageVector = NavigationItems.Settings.icon,
                    contentDescription = NavigationItems.Settings.label
                )
            },
            label = {
                Text(text = NavigationItems.Settings.label)
            }
        )
    }
}

