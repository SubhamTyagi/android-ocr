package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.ui.composables.DropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.EditTextPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import kotlin.random.Random


@Composable
fun SettingsTesseractParameter(/* viewModel: TesseractParametersViewModel=hiltViewModel(),*/
                               navController: NavController = rememberNavController(),
                               modifier: Modifier = Modifier
) {
    var pageSegMode by remember { mutableStateOf("best") }
    var ocrMode by remember { mutableStateOf("best") }
    var enableJapaneseChineseModifiers by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val context = LocalContext.current

        val names = context.resources.getStringArray(R.array.array_tess_psm_mode)
        val keys = context.resources.getStringArray(R.array.array_tess_psm_mode_values)
        //mock data


        Text("Page segmentation mode", style = MaterialTheme.typography.titleMedium)
        DropdownMenuPreference(
            title = "It direct how Tesseract splits image in lines of text and words.",
            options = names.toList(),
            selectedOption = pageSegMode,
            modifier = Modifier.fillMaxWidth(),
            onOptionSelected = { pageSegMode = it }

        )

        Text("Tesseract OCR Engine Mode(oem)", style = MaterialTheme.typography.titleMedium)
        DropdownMenuPreference(
            title = "Specify OCR Engine mode.",
            options = listOf("best", "fast", "standard"),
            selectedOption = ocrMode,
            modifier = Modifier.fillMaxWidth(),
            onOptionSelected = { ocrMode = it }

        )
        HorizontalDivider()
        Text("Japanese/Chinese Parameters", style = MaterialTheme.typography.titleMedium)
        SwitchPreference(
            title = "Set Tesseract Variable/Parameter",
            summary = "Set Tesseract parameters",
            checked = enableJapaneseChineseModifiers,
            onCheckedChange = {
                enableJapaneseChineseModifiers = it
            })
        AnimatedVisibility(enableJapaneseChineseModifiers) {
            JapaneseModifiers()
        }
        HorizontalDivider()
        ExtraTessPrams()
    }
}

@Composable
fun ExtraTessPrams() {

    Column {
        EditTextPreference(
            title = "Extra Tesseract parameters and values",
            summary = "Extra Tesseract parameters and values ",
            value = "not completed yet",
            onValueChange = {})
    }
}

@Composable
fun JapaneseModifiers() {


    Column {
        EditTextPreference(
            title = "Preserve Interword spaces",
            summary = "Preserve multiple inter word spaces",
            value = "0",
            onValueChange = {})

        EditTextPreference(
            title = "Chop Enable", summary = "Chop Enable", value = "T", onValueChange = {})

        EditTextPreference(
            title = "Use New State Cost",
            summary = "Use new state cost heuristics for segmentation state evaluation",
            value = "F",
            onValueChange = {})

        EditTextPreference(
            title = "Segment Segcost Rating",
            summary = "Incorporate segmentation cost in word rating",
            value = "F",
            onValueChange = {})

        EditTextPreference(
            title = "Enable New Segserach",
            summary = "Enable new segmentation search path. It could solve the problem of dividing one character to two characters.",
            value = "0",
            onValueChange = {})


        EditTextPreference(
            title = "Language Model Ngram On",
            summary = "Turn on/off the use of character ngram model",
            value = "F",
            onValueChange = {})

        EditTextPreference(
            title = "Textord Force Make Prop Words",
            summary = "Forace proportional word segmentation on all rows.",
            value = "F",
            onValueChange = {})
        EditTextPreference(
            title = "Edge Max Children per Outline",
            summary = "Max number of children inside a character outline. Increase this value id some of KANJI characters are not recognized(rejected) ",
            value = "40",
            onValueChange = {})

    }

}

@Preview
@Composable
fun SettingsImageProcessingScreenPreview() {
    CharacherRecognizerTheme {
        SettingsTesseractParameter()
    }
}