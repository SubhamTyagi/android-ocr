package io.github.subhamtyagi.ocr.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.ui.composables.BottomSheetState
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(viewModel: SettingsViewModel, navController: NavHostController, modifier: Modifier) {
    Scaffold(
        floatingActionButton = {
            MyFloatingActionButton()
        },
        modifier = modifier
    ) { innerPadding ->
        Column(Modifier.padding(20.dp)) {
            ProgressBar()
            DisplayLanguageName()
            ScreenshotImage(innerPadding)
            HistoryOfOCRItems()
            BottomSheetState()
        }
    }
}

@Composable
fun HistoryOfOCRItems() {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(5){
            HistoryItems()
        }
    }
}

@Composable
fun HistoryItems() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.drawable_default_image_60),
                contentScale = ContentScale.Crop,
                contentDescription = "Image on screen",
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Content::12",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

}

@Composable
private fun ScreenshotImage(innerPadding: PaddingValues) {
    Image(
        painter = painterResource(R.drawable.drawable_default_image_60),
        contentScale = ContentScale.Fit,
        contentDescription = "Image on screen",
        modifier = Modifier
            .heightIn(min = 100.dp, max = 180.dp)
            .fillMaxWidth()
    )
    Text(
        text = "Main area text",
        modifier = Modifier.padding(innerPadding)
    )
}

@Composable
private fun ProgressBar() {
    val showDownloadProgressBar by remember { mutableStateOf(true) }
    if (showDownloadProgressBar) {
        Row(Modifier.padding(bottom = 10.dp)) {
            LinearProgressIndicator(progress = {
                15f
            })
        }
    }
}

@Composable
private fun DisplayLanguageName() {
    Row {
        Text(
            text = "Selected Languages:"
        )
        Text(
            text = "Language Name:"
        )
    }
}


@Composable
fun MyFloatingActionButton(modifier: Modifier = Modifier) {
    FloatingActionButton(onClick = { /* Handle FAB click */ }) {
        Icon(Icons.Filled.Add, contentDescription = "Add")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CharacherRecognizerTheme {

    }
}