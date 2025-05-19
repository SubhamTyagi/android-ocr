package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.JCMState
import io.github.subhamtyagi.ocr.ui.composables.EditTextPreference
import io.github.subhamtyagi.ocr.ui.composables.KeyValueDropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.TesseractParametersViewModel

@Composable
fun SettingsTesseractParameter(
    tesseractParametersViewModel: TesseractParametersViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val pageSegMode = tesseractParametersViewModel.pageSegMode.collectAsState()
    val ocrMode = tesseractParametersViewModel.ocrMode.collectAsState()
    val enableJapaneseChineseModifiers =
        tesseractParametersViewModel.enableJCModifiers.collectAsState()
    val jcModifier = tesseractParametersViewModel.jCModifiers.collectAsState()

    SettingsTesseractParameterP(
        pageSegMode = pageSegMode,
        ocrMode = ocrMode,
        enableJapaneseChineseModifiers = enableJapaneseChineseModifiers,
        jcModifier = jcModifier,
        onPageSegModeChange = { tesseractParametersViewModel.updatePageSegMode(it) },
        onOcrModeChange = { tesseractParametersViewModel.updateOCRMode(it) },
        onEnableJCModifierChange = { tesseractParametersViewModel.updateEnableJCModifiers(it) },
        onJCModifierChange = { tesseractParametersViewModel.updateJCModifiers(it) },
    )
}

@Composable
fun SettingsTesseractParameterP(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    pageSegMode: State<Int>,
    ocrMode: State<Int>,
    enableJapaneseChineseModifiers: State<Boolean>,
    jcModifier: State<JCMState>,
    onPageSegModeChange: (Int) -> Unit,
    onOcrModeChange: (Int) -> Unit,
    onEnableJCModifierChange: (Boolean) -> Unit,
    onJCModifierChange: (JCMState) -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        var keyPSM = context.resources.getStringArray(R.array.array_tess_psm_mode)
        var valuePSM = context.resources.getIntArray(R.array.array_tess_psm_mode_values)
        val options = keyPSM.zip(valuePSM.toList())

        var keyOEM = context.resources.getStringArray(R.array.array_tess_oem_mode)
        var valueOEM = context.resources.getIntArray(R.array.array_tess_oem_mode_values)
        val optionsOEM = keyOEM.zip(valueOEM.toList())

        Text("Page segmentation mode", style = MaterialTheme.typography.titleMedium)

        KeyValueDropdownMenuPreference(
            title = "It direct how Tesseract splits image in lines of text and words.",
            options = options,
            selectedValue = pageSegMode.value,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onPageSegModeChange(it)
        }

        Text("Tesseract OCR Engine Mode(oem)", style = MaterialTheme.typography.titleMedium)

        KeyValueDropdownMenuPreference(
            title = "Specify OCR Engine mode.",
            options = optionsOEM,
            selectedValue = ocrMode.value,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onOcrModeChange(it)
        }

        HorizontalDivider()
        Text("Japanese/Chinese Parameters", style = MaterialTheme.typography.titleMedium)
        SwitchPreference(
            title = "Set Tesseract Variable/Parameter",
            summary = "Set Tesseract parameters",
            checked = enableJapaneseChineseModifiers.value,
            onCheckedChange = {
                onEnableJCModifierChange(it)
            })

        AnimatedVisibility(enableJapaneseChineseModifiers.value) {
            JapaneseModifiers(jcModifier, onJCModifierChange)
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
fun JapaneseModifiers(jcModifierState: State<JCMState>, onJCModifierChange: (JCMState) -> Unit) {
    Column {
        EditTextPreference(
            title = "Preserve Interword spaces",
            summary = "Preserve multiple inter word spaces",
            value = jcModifierState.value.preserveInterWordSpaces,
            onValueChange = {
                jcModifierState.value.preserveInterWordSpaces = it
                onJCModifierChange(jcModifierState.value)
            })

        EditTextPreference(
            title = "Chop Enable",
            summary = "Chop Enable",
            value = jcModifierState.value.chopEnable,
            onValueChange = {
                jcModifierState.value.chopEnable = it
                onJCModifierChange(jcModifierState.value)
            })



        EditTextPreference(
            title = "Language Model Ngram On",
            summary = "Turn on/off the use of character ngram model",
            value = jcModifierState.value.languageNgramOn,
            onValueChange = {
                jcModifierState.value.languageNgramOn = it
                onJCModifierChange(jcModifierState.value)
            })

        EditTextPreference(
            title = "Textord Force Make Prop Words",
            summary = "Force proportional word segmentation on all rows.",
            value = jcModifierState.value.textortForceMakePropWords,
            onValueChange = {
                jcModifierState.value.textortForceMakePropWords = it
                onJCModifierChange(jcModifierState.value)
            })
        EditTextPreference(
            title = "Edge Max Children per Outline",
            summary = "Max number of children inside a character outline. Increase this value id some of KANJI characters are not recognized(rejected) ",
            value = jcModifierState.value.edgeMaxChildrenPerOutline,
            onValueChange = {
                jcModifierState.value.copy(edgeMaxChildrenPerOutline = it)
                onJCModifierChange(jcModifierState.value)
            })
    }
}

@Preview
@Composable
fun SettingsImageProcessingScreenPreview() {
    CharacherRecognizerTheme {
        var pageSegMode = remember { mutableIntStateOf(6) }
        var ocrMode = remember { mutableIntStateOf(0) }
        var enableJapaneseChineseModifiers = remember { mutableStateOf(false) }
        val jcModifier = remember { mutableStateOf(JCMState()) }

        SettingsTesseractParameterP(
            pageSegMode = pageSegMode,
            ocrMode = ocrMode,
            enableJapaneseChineseModifiers = enableJapaneseChineseModifiers,
            jcModifier = jcModifier,
            onPageSegModeChange = { pageSegMode.value = it },
            onOcrModeChange = {
                ocrMode.value = it
            },
            onEnableJCModifierChange = { enableJapaneseChineseModifiers.value = it },
            onJCModifierChange = { jcModifier.value = it },
        )
    }
}