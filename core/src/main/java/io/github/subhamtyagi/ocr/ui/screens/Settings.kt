package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.ui.NavigationItems
import io.github.subhamtyagi.ocr.ui.composables.DropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    var advancedTessEnabled = settingsViewModel.advancedTessEnabled.collectAsState()
    var useGrayscale = settingsViewModel.useGrayscale.collectAsState()
    var persistData = settingsViewModel.persistData.collectAsState()
    var tessDataSource = settingsViewModel.tessDataSource.collectAsState()
    var tile = settingsViewModel.tile.collectAsState()

    SettingsScreenP(
        navController = navController,
        advancedTessEnabled = advancedTessEnabled,
        useGrayscale = useGrayscale,
        persistData = persistData,
        tessDataSource = tessDataSource,
        tile = tile,
        onDataSourceChange = {
            settingsViewModel.updateTessDataSource(it)
        },
        onUseGrayscaleChange = { settingsViewModel.updateUseGrayscale(it) },
        onPersistDataChange = { settingsViewModel.updatePersistData(it) },
        onTileChange = { settingsViewModel.updateTile(it) },
        onAdvanceTessEnabledChange = { settingsViewModel.updateAdvancedTessEnabled(it) },
    )

}

@Composable
fun SettingsScreenP(
    navController: NavController,
    tessDataSource: State<String>,
    advancedTessEnabled: State<Boolean>,
    useGrayscale: State<Boolean>,
    persistData: State<Boolean>,
    tile: State<Boolean>,
    onDataSourceChange: (String) -> Unit,
    onAdvanceTessEnabledChange: (Boolean) -> Unit,
    onUseGrayscaleChange: (Boolean) -> Unit,
    onPersistDataChange: (Boolean) -> Unit,
    onTileChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Tesseract Data Source", style = MaterialTheme.typography.titleMedium)

        DropdownMenuPreference(
            title = "Select Tesseract Data Type",
            options = listOf("best", "fast", "standard"),
            selectedOption = tessDataSource.value,
            modifier = Modifier.fillMaxWidth(),
            onOptionSelected = {
                onDataSourceChange(it)
            }
        )

        HorizontalDivider()

        Text("Advanced Tesseract Settings", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Set Tesseract Variable/Parameter",
            summary = "Advance tesseract option. Used with Caution; only use these option if you know what are you doing",
            checked = advancedTessEnabled.value,
            onCheckedChange = {
                onAdvanceTessEnabledChange(it)
            }
        )

        AnimatedVisibility(advancedTessEnabled.value) {
            Button(
                onClick = { navController.navigate(NavigationItems.SettingsTesseractParameter.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tesseract Variable/Parameter Settings")
            }
        }

        HorizontalDivider()

        Text("Image Enhancement", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Use Image Enhancement for OCR",
            summary = "Pre-process image for enhanced accuracy",
            checked = useGrayscale.value,
            onCheckedChange = {
                onUseGrayscaleChange(it)
            }
        )

        AnimatedVisibility(useGrayscale.value) {
            Button(
                onClick = { navController.navigate(NavigationItems.SettingsImageProcessing.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Image Processing Functions")
            }
        }
        HorizontalDivider()

        Text("Other Settings", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Add Tile for floating window",
            summary = "Use tile to select text on screen window",
            checked = tile.value,
            onCheckedChange = {
                onTileChange(it)
            }
        )

        SwitchPreference(
            title = "Persist Data",
            summary = "Save history and show them on Home Screen",
            checked = persistData.value,
            onCheckedChange = {
                onPersistDataChange(it)
            }
        )

        Text("About", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
        Button(
            onClick = {
                navController.navigate("https://github.com/")
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Source Code")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    CharacherRecognizerTheme {

        var advancedTessEnabled = remember { mutableStateOf(false) }
        var useGrayscale = remember { mutableStateOf(false) }
        var persistData = remember { mutableStateOf(false) }
        var tessDataSource = remember { mutableStateOf("best") }
        var tile = remember { mutableStateOf(false) }

        SettingsScreenP(
            navController = rememberNavController(),
            advancedTessEnabled = advancedTessEnabled,
            useGrayscale = useGrayscale,
            persistData = persistData,
            tessDataSource = tessDataSource,
            tile = tile,
            onDataSourceChange = {
                tessDataSource.value = it
            },
            onUseGrayscaleChange = { useGrayscale.value = it },
            onPersistDataChange = { persistData.value = it },
            onTileChange = { tile.value = it },
            onAdvanceTessEnabledChange = { advancedTessEnabled.value = it },
        )
    }
}