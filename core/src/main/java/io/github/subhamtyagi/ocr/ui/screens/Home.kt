package io.github.subhamtyagi.ocr.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.History
import io.github.subhamtyagi.ocr.ui.composables.ShowBottomSheet
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme

@Composable
fun HomeScreen(/*viewModel: SettingsViewModel,*/ navController: NavHostController,
               modifier: Modifier = Modifier
) {
    val list = arrayListOf<History>()
    repeat(5) {
        list.add(
            History(
                title = "Title $it",
                ocrText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                imagePath = "Not available"
            )
        )
    }


    Scaffold(
        floatingActionButton = {
            MyFloatingActionButton()
        }, modifier = modifier
    ) { paddingValues ->
        Column {
            var n = paddingValues
            ProgressBar()
            DisplayLanguageName()
            HistoryOfOCRItems(list)
        }
    }
}

@Composable
fun MyFloatingActionButton(modifier: Modifier = Modifier) {
    FloatingActionButton(onClick = { /* Handle FAB click */ }) {
        Icon(Icons.Filled.Add, contentDescription = "Add")
    }
}

@Composable
private fun ProgressBar() {
    val showDownloadProgressBar by remember { mutableStateOf(true) }
    if (showDownloadProgressBar) {
        Row {
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
            text = "Selected Languages:", modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )
        Text(
            text = "English,Spanish,Czech", modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )
    }
}

@Composable
fun HistoryOfOCRItems(historyList: ArrayList<History>) {
    Text(
        text = "Recents",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(start = 8.dp, top = 16.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(historyList) { historyItem ->
            var showOcrResult by remember { mutableStateOf(false) }
            HistoryItems(
                title = historyItem.title,
                ocrText = historyItem.ocrText,
                image = R.drawable.drawable_default_image_60,
                onClick = { showOcrResult = true })
            if (showOcrResult) {
                ShowBottomSheet(historyItem, dismiss = { showOcrResult = false })
            }
        }

    }
}


@Composable
fun HistoryItems(title: String, ocrText: String, @DrawableRes image: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .sizeIn(maxHeight = 120.dp)
            .clickable {
                onClick()
            }, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(image),
                contentScale = ContentScale.Crop,
                contentDescription = "Image on screen",
                modifier = Modifier.size(120.dp),
            )
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = ocrText, modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CharacherRecognizerTheme {
        HomeScreen(rememberNavController())
    }
}