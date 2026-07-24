package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.ImageProcessingViewModel

@Composable
fun ImageProcessingScreen(
    modifier: Modifier = Modifier,
    imageProcessingViewModel: ImageProcessingViewModel = hiltViewModel(),
) {
    val enhanceContrast by imageProcessingViewModel.enhanceContrast.collectAsStateWithLifecycle()
    val unSharpMasking by imageProcessingViewModel.unsharpMasking.collectAsStateWithLifecycle()
    val otsu by imageProcessingViewModel.otsu.collectAsStateWithLifecycle()
    val deSkew by imageProcessingViewModel.deskew.collectAsStateWithLifecycle()

    ImageProcessingContent(
        modifier = modifier,
        enhanceContrast = enhanceContrast,
        unSharpMasking = unSharpMasking,
        otsu = otsu,
        deSkew = deSkew,
        onEnhanceContrastChange = imageProcessingViewModel::updateEnhanceContrast,
        onDeSkewChange = imageProcessingViewModel::updateDeSkew,
        onOTSUChange = imageProcessingViewModel::updateOTSU,
        onUnSharpMaskingChange = imageProcessingViewModel::updateUnSharpMasking
    )
}

@Composable
fun ImageProcessingContent(
    enhanceContrast: Boolean,
    unSharpMasking: Boolean,
    otsu: Boolean,
    deSkew: Boolean,
    onEnhanceContrastChange: (Boolean) -> Unit,
    onUnSharpMaskingChange: (Boolean) -> Unit,
    onOTSUChange: (Boolean) -> Unit,
    onDeSkewChange: (Boolean) -> Unit,
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
            SwitchPreference(
                title = stringResource(R.string.perform_contrast),
                summary = stringResource(R.string.contrast_summary),
                checked = enhanceContrast,
                onCheckedChange = onEnhanceContrastChange
            )

            SwitchPreference(
                title = stringResource(R.string.un_sharp_masking),
                summary = stringResource(R.string.un_sharp_masking_summary),
                checked = unSharpMasking,
                onCheckedChange = onUnSharpMaskingChange
            )
            SwitchPreference(
                title = stringResource(R.string.otsu_threshold),
                summary = stringResource(R.string.otsu_summary),
                checked = otsu,
                onCheckedChange = onOTSUChange
            )
            SwitchPreference(
                title = stringResource(R.string.deskew_image),
                summary = stringResource(R.string.deskew_summary),
                checked = deSkew,
                onCheckedChange = onDeSkewChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageProcessingScreenPreview() {
    CharacherRecognizerTheme {

        var enhanceContrast by remember { mutableStateOf(false) }
        var unSharpMasking by remember { mutableStateOf(false) }
        var otsu by remember { mutableStateOf(false) }
        var deSkew by remember { mutableStateOf(false) }

        ImageProcessingContent(
            enhanceContrast = enhanceContrast,
            unSharpMasking = unSharpMasking,
            otsu = otsu,
            deSkew = deSkew,
            onEnhanceContrastChange = { enhanceContrast = it },
            onDeSkewChange = { deSkew = it },
            onOTSUChange = { otsu = it },
            onUnSharpMaskingChange = { unSharpMasking = it }
        )
    }
}
