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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.JCMState
import io.github.subhamtyagi.ocr.ui.composables.EditTextPreference
import io.github.subhamtyagi.ocr.ui.composables.KeyValueDropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.TesseractParametersViewModel

@Composable
fun TesseractParametersScreen(
    modifier: Modifier = Modifier,
    tesseractParametersViewModel: TesseractParametersViewModel = hiltViewModel(),
) {
    val pageSegMode by tesseractParametersViewModel.pageSegMode.collectAsStateWithLifecycle()
    val ocrMode by tesseractParametersViewModel.ocrMode.collectAsStateWithLifecycle()
    val enableJapaneseChineseModifiers by tesseractParametersViewModel.enableJCModifiers.collectAsStateWithLifecycle()
    val jcModifier by tesseractParametersViewModel.jCModifiers.collectAsStateWithLifecycle()

    TesseractParametersContent(
        modifier = modifier,
        pageSegMode = pageSegMode,
        ocrMode = ocrMode,
        enableJapaneseChineseModifiers = enableJapaneseChineseModifiers,
        jcModifier = jcModifier,
        onPageSegModeChange = tesseractParametersViewModel::updatePageSegMode,
        onOcrModeChange = tesseractParametersViewModel::updateOCRMode,
        onEnableJCModifierChange = tesseractParametersViewModel::updateEnableJCModifiers,
        onJCModifierChange = tesseractParametersViewModel::updateJCModifiers,
    )
}

@Composable
fun TesseractParametersContent(
    pageSegMode: Int,
    ocrMode: Int,
    enableJapaneseChineseModifiers: Boolean,
    jcModifier: JCMState,
    onPageSegModeChange: (Int) -> Unit,
    onOcrModeChange: (Int) -> Unit,
    onEnableJCModifierChange: (Boolean) -> Unit,
    onJCModifierChange: (JCMState) -> Unit,
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
            val context = LocalContext.current
            val keyPSM = context.resources.getStringArray(R.array.array_tess_psm_mode)
            val valuePSM = context.resources.getIntArray(R.array.array_tess_psm_mode_values)
            val options = keyPSM.zip(valuePSM.toList())

            val keyOEM = context.resources.getStringArray(R.array.array_tess_oem_mode)
            val valueOEM = context.resources.getIntArray(R.array.array_tess_oem_mode_values)
            val optionsOEM = keyOEM.zip(valueOEM.toList())

            Text(
                stringResource(R.string.page_segmentation_mode),
                style = MaterialTheme.typography.titleMedium
            )

            KeyValueDropdownMenuPreference(
                title = stringResource(R.string.it_direct_how_tesseract_splits_image_in_lines_of_text_and_words),
                options = options,
                selectedValue = pageSegMode,
                modifier = Modifier.fillMaxWidth(),
            ) {
                onPageSegModeChange(it)
            }

            Text(
                stringResource(R.string.tesseract_ocr_engine_mode_oem),
                style = MaterialTheme.typography.titleMedium
            )

            KeyValueDropdownMenuPreference(
                title = stringResource(R.string.specify_ocr_engine_mode),
                options = optionsOEM,
                selectedValue = ocrMode,
                modifier = Modifier.fillMaxWidth(),
            ) {
                onOcrModeChange(it)
            }

            HorizontalDivider()
            Text(
                stringResource(R.string.japanese_chinese_parameters),
                style = MaterialTheme.typography.titleMedium
            )
            SwitchPreference(
                title = stringResource(R.string.set_tesseract_variable_parameter),
                summary = stringResource(R.string.set_tesseract_parameters),
                checked = enableJapaneseChineseModifiers,
                onCheckedChange = onEnableJCModifierChange
            )

            AnimatedVisibility(enableJapaneseChineseModifiers) {
                JapaneseModifiers(jcModifier, onJCModifierChange)
            }
            HorizontalDivider()
            ExtraTessPrams()
        }
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
fun JapaneseModifiers(jcModifier: JCMState, onJCModifierChange: (JCMState) -> Unit) {
    Column {
        EditTextPreference(
            title = stringResource(R.string.preserve_interword_spaces),
            summary = stringResource(R.string.preserve_multiple_inter_word_spaces),
            value = jcModifier.preserveInterWordSpaces,
            onValueChange = {
                onJCModifierChange(jcModifier.copy(preserveInterWordSpaces = it))
            })

        EditTextPreference(
            title = stringResource(R.string.chop_enable),
            summary = stringResource(R.string.chop_enable),
            value = jcModifier.chopEnable,
            onValueChange = {
                onJCModifierChange(jcModifier.copy(chopEnable = it))
            })

        EditTextPreference(
            title = stringResource(R.string.language_model_ngram_on),
            summary = stringResource(R.string.turn_on_off_the_use_of_character_ngram_model),
            value = jcModifier.languageNgramOn,
            onValueChange = {
                onJCModifierChange(jcModifier.copy(languageNgramOn = it))
            })

        EditTextPreference(
            title = stringResource(R.string.textord_force_make_prop_words),
            summary = stringResource(R.string.force_proportional_word_segmentation_on_all_rows),
            value = jcModifier.textortForceMakePropWords,
            onValueChange = {
                onJCModifierChange(jcModifier.copy(textortForceMakePropWords = it))
            })
        EditTextPreference(
            title = stringResource(R.string.edge_max_children_per_outline),
            summary = stringResource(R.string.max_number_of_children_inside_a_character_outline_increase_this_value_id_some_of_kanji_characters_are_not_recognized_rejected),
            value = jcModifier.edgeMaxChildrenPerOutline,
            onValueChange = {
                onJCModifierChange(jcModifier.copy(edgeMaxChildrenPerOutline = it))
            })
    }
}

@Preview
@Composable
fun TesseractParametersScreenPreview() {
    CharacherRecognizerTheme {
        var pageSegMode by remember { mutableIntStateOf(6) }
        var ocrMode by remember { mutableIntStateOf(0) }
        var enableJapaneseChineseModifiers by remember { mutableStateOf(false) }
        var jcModifier by remember { mutableStateOf(JCMState()) }

        TesseractParametersContent(
            pageSegMode = pageSegMode,
            ocrMode = ocrMode,
            enableJapaneseChineseModifiers = enableJapaneseChineseModifiers,
            jcModifier = jcModifier,
            onPageSegModeChange = { pageSegMode = it },
            onOcrModeChange = { ocrMode = it },
            onEnableJCModifierChange = { enableJapaneseChineseModifiers = it },
            onJCModifierChange = { jcModifier = it },
        )
    }
}
