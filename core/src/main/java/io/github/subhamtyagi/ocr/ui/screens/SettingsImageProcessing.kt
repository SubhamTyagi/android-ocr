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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.ui.composables.DropdownMenuPreference
import io.github.subhamtyagi.ocr.ui.composables.EditTextPreference
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme

@Composable
fun SettingsImageProcessingScreen(
    /* viewModel: ImageProcessingViewModel=hiltViewModel(),*/
    navController: NavController = rememberNavController(),

    ) {
    var enhanceContrast by remember { mutableStateOf(false) }
    var unsharpMasking by remember { mutableStateOf(false) }
    var otsu by remember { mutableStateOf(false) }
    var deSkew by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SwitchPreference(
            title = stringResource(R.string.perform_contrast),
            summary = stringResource(R.string.contrast_summary),
            checked = enhanceContrast,
            onCheckedChange = { /*viewModel.update(it)*/
                enhanceContrast = it
            })

        SwitchPreference(
            title = stringResource(R.string.un_sharp_masking),
            summary = stringResource(R.string.un_sharp_masking_summary),
            checked = unsharpMasking,
            onCheckedChange = { /*viewModel.update(it)*/
                unsharpMasking = it
            })
        SwitchPreference(
            title = stringResource(R.string.otsu_threshold),
            summary = stringResource(R.string.otsu_summary),
            checked = otsu,
            onCheckedChange = { /*viewModel.update(it)*/
                otsu = it
            })
        SwitchPreference(
            title = stringResource(R.string.deskew_image),
            summary = stringResource(R.string.deskew_summary),
            checked = deSkew,
            onCheckedChange = { /*viewModel.update(it)*/
                deSkew = it
            })
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenIPPreveiw() {
    CharacherRecognizerTheme {
        SettingsImageProcessingScreen()
    }
}