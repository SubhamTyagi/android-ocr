package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import io.github.subhamtyagi.ocr.ui.composables.SwitchPreference
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel

@Composable
fun DownloadLanguageDataScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column {
        Text("hello")
        SwitchPreference(
            title = "name",
            checked = true,
            onCheckedChange = { viewModel.updatePersistData(it) })
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDownloadLanguageDataScreen() {
    CharacherRecognizerTheme {

    }
}