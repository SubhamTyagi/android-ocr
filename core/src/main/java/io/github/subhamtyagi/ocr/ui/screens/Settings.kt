package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.subhamtyagi.ocr.ui.composables.DropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.MultiSelectPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val tessDataSource by viewModel.tessDataSource.collectAsState()
    val selectedLanguages by viewModel.selectedLanguages.collectAsState()
    val advancedTessEnabled by viewModel.advancedTessEnabled.collectAsState()
    val useGrayscale by viewModel.useGrayscale.collectAsState()
    val persistData by viewModel.persistData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tesseract Data", style = MaterialTheme.typography.titleMedium)

        DropdownMenuPreference(
            title = "Select Tesseract Data Type",
            options = listOf("best", "fast", "standard"),
            selectedOption = tessDataSource,
            onOptionSelected = { viewModel.updateTessDataSource(it) }
        )

        MultiSelectPreference(
            title = "Select OCR Languages",
            options = listOf("eng", "spa", "fra", "deu"),
            selectedOptions = selectedLanguages,
            onSelectionChange = { viewModel.updateSelectedLanguages(it) }
        )

        HorizontalDivider()

        Text("Advanced Tesseract Settings", style = MaterialTheme.typography.titleMedium)

        SwitchPreference(
            title = "Set Tesseract Variable/Parameter",
            checked = advancedTessEnabled,
            onCheckedChange = { viewModel.updateAdvancedTessEnabled(it) }
        )

        if (advancedTessEnabled) {
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
            onCheckedChange = { viewModel.updateUseGrayscale(it) }
        )

        if (useGrayscale) {
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
            onCheckedChange = { viewModel.updatePersistData(it) }
        )

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

    }
}