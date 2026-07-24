package io.github.subhamtyagi.ocr.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val advancedTessEnabled by settingsViewModel.advancedTessEnabled.collectAsStateWithLifecycle()
    val imageProcessingFunctions by settingsViewModel.useImageProcessing.collectAsStateWithLifecycle()
    val tile by settingsViewModel.tile.collectAsStateWithLifecycle()
    val showLanguageDialog by settingsViewModel.showLanguageDialog.collectAsStateWithLifecycle()

    SettingsContent(
        modifier = modifier,
        advancedTessEnabled = advancedTessEnabled,
        imageProcessingFunctions = imageProcessingFunctions,
        tile = tile,
        showLanguageDialog = showLanguageDialog,
        onImageProcessingFunctionsChange = settingsViewModel::updateUseGrayscale,
        onTileChange = settingsViewModel::updateTile,
        onAdvanceTessEnabledChange = settingsViewModel::updateAdvancedTessEnabled,
        onShowLanguageDialogChange = settingsViewModel::updateShowLanguageDialog,
        onNavigateToTesseractSettings = {
            navController.navigate(NavigationItems.SettingsTesseractParameter.route)
        },
        onNavigateToImageProcessing = {
            navController.navigate(NavigationItems.SettingsImageProcessing.route)
        }
    )
}

@Composable
fun SettingsContent(
    advancedTessEnabled: Boolean,
    imageProcessingFunctions: Boolean,
    tile: Boolean,
    showLanguageDialog: Boolean,
    onAdvanceTessEnabledChange: (Boolean) -> Unit,
    onImageProcessingFunctionsChange: (Boolean) -> Unit,
    onTileChange: (Boolean) -> Unit,
    onShowLanguageDialogChange: (Boolean) -> Unit,
    onNavigateToTesseractSettings: () -> Unit,
    onNavigateToImageProcessing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.advanced_tesseract_settings),
                style = MaterialTheme.typography.titleMedium
            )
            SwitchPreference(
                title = stringResource(R.string.set_tesseract_variable_parameter),
                summary = stringResource(R.string.advance_tesseract_option_used_with_caution_only_use_these_option_if_you_know_what_are_you_doing),
                checked = advancedTessEnabled,
                onCheckedChange = onAdvanceTessEnabledChange
            )

            AnimatedVisibility(advancedTessEnabled) {
                Button(
                    onClick = onNavigateToTesseractSettings,
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
                checked = imageProcessingFunctions,
                onCheckedChange = onImageProcessingFunctionsChange
            )

            AnimatedVisibility(imageProcessingFunctions) {
                Button(
                    onClick = onNavigateToImageProcessing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.image_processing_functions))
                }
            }
            HorizontalDivider()
            Text(stringResource(R.string.other_settings), style = MaterialTheme.typography.titleMedium)
            SwitchPreference(
                title = stringResource(R.string.add_tile_for_floating_window),
                summary = stringResource(R.string.add_tile_in_action_center_to_select_text_on_screen_window),
                checked = tile,
                onCheckedChange = onTileChange
            )
            SwitchPreference(
                title = stringResource(R.string.show_language_selection_dialog_title),
                summary = stringResource(R.string.show_language_selection_dialog_summary),
                checked = showLanguageDialog,
                onCheckedChange = onShowLanguageDialogChange
            )

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
}


@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    CharacherRecognizerTheme {

        var advancedTessEnabled by remember { mutableStateOf(false) }
        var useGrayscale by remember { mutableStateOf(false) }
        var tile by remember { mutableStateOf(false) }
        var showLanguageDialog by remember { mutableStateOf(false) }

        SettingsContent(
            advancedTessEnabled = advancedTessEnabled,
            imageProcessingFunctions = useGrayscale,
            tile = tile,
            showLanguageDialog = showLanguageDialog,
            onImageProcessingFunctionsChange = { useGrayscale = it },
            onTileChange = { tile = it },
            onAdvanceTessEnabledChange = { advancedTessEnabled = it },
            onShowLanguageDialogChange = { showLanguageDialog = it },
            onNavigateToTesseractSettings = {},
            onNavigateToImageProcessing = {}
        )
    }
}
