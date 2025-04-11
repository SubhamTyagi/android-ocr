package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.ui.composables.DropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.MultiSelectPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(/* viewModel: SettingsViewModel,*/
                   navController: NavController = rememberNavController(),
                   modifier: Modifier = Modifier
) {/*val tessDataSource by viewModel.tessDataSource.collectAsState()
    val selectedLanguages by viewModel.selectedLanguages.collectAsState()
    val advancedTessEnabled by viewModel.advancedTessEnabled.collectAsState()
    val useGrayscale by viewModel.useGrayscale.collectAsState()
    val persistData by viewModel.persistData.collectAsState()*/


    var advancedTessEnabled by remember { mutableStateOf(false) }
    var useGrayscale by remember { mutableStateOf(false) }
    var persistData by remember { mutableStateOf(false) }
    var selectedLanguages by remember { mutableStateOf("best") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tesseract Data Source", style = MaterialTheme.typography.titleMedium)

        DropdownMenuPreference(
            title = "Select Tesseract Data Type",
            options = listOf("best", "fast", "standard"),
            selectedOption = selectedLanguages /*tessDataSource*/,
            modifier = Modifier.fillMaxWidth(),
            onOptionSelected = { selectedLanguages = it/*viewModel.updateTessDataSource(it)*/ }

        )

        HorizontalDivider()

        Text("Advanced Tesseract Settings", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Set Tesseract Variable/Parameter",
            checked = advancedTessEnabled,
            onCheckedChange = { /*viewModel.updateAdvancedTessEnabled(it)*/
                advancedTessEnabled = it
            })

        AnimatedVisibility(advancedTessEnabled) {
            Button(
                onClick = { navController.navigate("variable_settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tesseract Variable/Parameter Settings")
            }
        }

        HorizontalDivider()

        Text("Image Enhancement", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Use Grayscale Image for OCR",
            checked = useGrayscale,
            onCheckedChange = { /*viewModel.updateUseGrayscale(it)*/
                useGrayscale = it
            })

        AnimatedVisibility(useGrayscale) {
            Button(
                onClick = { navController.navigate("image_processing_settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Image Processing Functions")
            }
        }

        HorizontalDivider()

        Text("Other Settings", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Persist Data",
            checked = persistData,
            onCheckedChange = { persistData = it/*viewModel.updatePersistData(it)*/ })

        HorizontalDivider()

        Text("About", style = MaterialTheme.typography.titleMedium)

        Button(
            onClick = { navController.navigate("https://github.com/") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Source Code")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    CharacherRecognizerTheme {
        SettingsScreen()
    }
}