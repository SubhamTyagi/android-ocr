package io.github.subhamtyagi.ocr.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.ui.NavigationItems
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
    var imageProcessingFunctions = settingsViewModel.useImageProcessing.collectAsState()
    var persistData = settingsViewModel.persistData.collectAsState()
    var tile = settingsViewModel.tile.collectAsState()

    SettingsScreenP(
        navController = navController,
        advancedTessEnabled = advancedTessEnabled,
        imageProcessingFunctions = imageProcessingFunctions,
        persistData = persistData,
        tile = tile,

        onImageProcessingFunctionsChange = { settingsViewModel.updateUseGrayscale(it) },
        onPersistDataChange = { settingsViewModel.updatePersistData(it) },
        onTileChange = { settingsViewModel.updateTile(it) },
        onAdvanceTessEnabledChange = { settingsViewModel.updateAdvancedTessEnabled(it) },
    )
}

@Composable
fun SettingsScreenP(
    navController: NavController,
    advancedTessEnabled: State<Boolean>,
    imageProcessingFunctions: State<Boolean>,
    persistData: State<Boolean>,
    tile: State<Boolean>,
    onAdvanceTessEnabledChange: (Boolean) -> Unit,
    onImageProcessingFunctionsChange: (Boolean) -> Unit,
    onPersistDataChange: (Boolean) -> Unit,
    onTileChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.advanced_tesseract_settings), style = MaterialTheme.typography.titleMedium)
        SwitchPreference(
            title = stringResource(R.string.set_tesseract_variable_parameter),
            summary = stringResource(R.string.advance_tesseract_option_used_with_caution_only_use_these_option_if_you_know_what_are_you_doing),
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
                Text(stringResource(R.string.tesseract_variable_parameter_settings))
            }
        }

        HorizontalDivider()
        Text("Image Enhancement", style = MaterialTheme.typography.titleMedium)
        SwitchPreference(
            title = stringResource(R.string.use_image_enhancement_for_ocr),
            summary = stringResource(R.string.pre_process_image_for_enhanced_accuracy),
            checked = imageProcessingFunctions.value,
            onCheckedChange = {
                onImageProcessingFunctionsChange(it)
            })

        AnimatedVisibility(imageProcessingFunctions.value) {
            Button(
                onClick = { navController.navigate(NavigationItems.SettingsImageProcessing.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.image_processing_functions))
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
            })
        SwitchPreference(
            title = stringResource(R.string.persist_data),
            summary = stringResource(R.string.save_history_and_show_them_on_home_screen),
            checked = persistData.value,
            onCheckedChange = {
                onPersistDataChange(it)
            })

        Text(stringResource(R.string.about), style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
        val context = LocalContext.current
        Button(
            onClick = {
                val intent =
                    Intent(Intent.ACTION_VIEW, "https://github.com/SubhamTyagi/android-ocr".toUri())
                context.startActivity(intent)
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.source_code))
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
        var tile = remember { mutableStateOf(false) }

        SettingsScreenP(
            navController = rememberNavController(),
            advancedTessEnabled = advancedTessEnabled,
            imageProcessingFunctions = useGrayscale,
            persistData = persistData,
            tile = tile,
            onImageProcessingFunctionsChange = { useGrayscale.value = it },
            onPersistDataChange = { persistData.value = it },
            onTileChange = { tile.value = it },
            onAdvanceTessEnabledChange = { advancedTessEnabled.value = it },
        )
    }
}