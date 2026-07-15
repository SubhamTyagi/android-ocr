package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.ImageProcessingViewModel

@Composable
fun SettingsImageProcessingScreen(
    imageProcessingViewModel: ImageProcessingViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    var enhanceContrast = imageProcessingViewModel.enhanceContrast.collectAsState()
    var unSharpMasking = imageProcessingViewModel.unsharpMasking.collectAsState()
    var otsu = imageProcessingViewModel.otsu.collectAsState()
    var deSkew = imageProcessingViewModel.deskew.collectAsState()

    SettingsImageProcessingScreenP(
        navController = navController,
        enhanceContrast = enhanceContrast,
        unSharpMasking = unSharpMasking,
        otsu = otsu,
        deSkew = deSkew,
        onEnhanceContrastChange = {
            imageProcessingViewModel.updateEnhanceContrast(it)
        },
        onDeSkewChange = {
            imageProcessingViewModel.updateDeSkew(it)
        },
        onOTSUChange = {
            imageProcessingViewModel.updateOTSU(it)
        },
        onUnSharpMaskingChange = {
            imageProcessingViewModel.updateUnSharpMasking(it)
        })
}

@Composable
fun SettingsImageProcessingScreenP(
    navController: NavController = rememberNavController(),
    enhanceContrast: State<Boolean>,
    unSharpMasking: State<Boolean>,
    otsu: State<Boolean>,
    deSkew: State<Boolean>,
    onEnhanceContrastChange: (Boolean) -> Unit,
    onUnSharpMaskingChange: (Boolean) -> Unit,
    onOTSUChange: (Boolean) -> Unit,
    onDeSkewChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SwitchPreference(
            title = stringResource(R.string.perform_contrast),
            summary = stringResource(R.string.contrast_summary),
            checked = enhanceContrast.value,
            onCheckedChange = {
                onEnhanceContrastChange(it)
            })

        SwitchPreference(
            title = stringResource(R.string.un_sharp_masking),
            summary = stringResource(R.string.un_sharp_masking_summary),
            checked = unSharpMasking.value,
            onCheckedChange = {
                onUnSharpMaskingChange(it)
            })
        SwitchPreference(
            title = stringResource(R.string.otsu_threshold),
            summary = stringResource(R.string.otsu_summary),
            checked = otsu.value,
            onCheckedChange = {
                onOTSUChange(it)
            })
        SwitchPreference(
            title = stringResource(R.string.deskew_image),
            summary = stringResource(R.string.deskew_summary),
            checked = deSkew.value,
            onCheckedChange = {
                onDeSkewChange(it)
            })
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenIPPreview() {
    CharacherRecognizerTheme {

        var enhanceContrast = remember { mutableStateOf(false) }
        var unSharpMasking = remember { mutableStateOf(false) }
        var otsu = remember { mutableStateOf(false) }
        var deSkew = remember { mutableStateOf(false) }

        SettingsImageProcessingScreenP(

            enhanceContrast = enhanceContrast,
            unSharpMasking = unSharpMasking,
            otsu = otsu,
            deSkew = deSkew,
            onEnhanceContrastChange = {
                enhanceContrast.value = it
            },
            onDeSkewChange = {
                deSkew.value = it
            },
            onOTSUChange = {
                otsu.value = it
            },
            onUnSharpMaskingChange = {
                unSharpMasking.value = it
            })

    }
}