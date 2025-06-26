package io.github.subhamtyagi.ocr.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.downloader.DownloadResult
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.DownloadLanguageViewModel
import kotlin.random.Random

@Composable
fun DownloadLanguageDataScreen(
    downloadViewModel: DownloadLanguageViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val TAG = "DownloadLDScreen"
    val context = LocalContext.current
    val downloadedLanguages by downloadViewModel.downloadedLanguages.collectAsState()
    val selectedLanguages by downloadViewModel.selectedLanguages.collectAsState()
    val progressMap by downloadViewModel.downloadProgressMap.collectAsState()
    val downloadResultFlow = downloadViewModel.downloadResultFlow

    val languageList = remember(selectedLanguages, downloadedLanguages) {
        downloadViewModel.getLanguagesList(selectedLanguages)
    }

    LaunchedEffect(Unit) {
        downloadViewModel.checkDownloadedLanguages(languageList)
        // downloadViewModel.observeTessDirectory(languageList)

        downloadResultFlow.collect { result ->
            when (result) {
                is DownloadResult.Success -> {
                    Toast.makeText(
                        context,
                        "Downloaded ${result.language.name} successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DownloadResult.Failure -> {
                    Toast.makeText(
                        context,
                        "Failed to download ${result.language.name}: ${result.reason}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    DownloadLanguageDataScreenP(
        languageList = languageList,
        downloadProgressMap = progressMap,
        navController = navController,
        onSelected = { language, value ->
            Log.d(TAG, "DownloadLanguageDataScreen: language=$language , new value=$value")
            downloadViewModel.updateSelectedLanguages(language, value)
        },
        downloadLanguage = { lang -> downloadViewModel.downloadLanguage(lang) },
        deleteLanguage = { lang -> downloadViewModel.deleteLanguage(lang) })

}

@Composable
fun DownloadLanguageDataScreenP(
    languageList: List<Language>,
    downloadProgressMap: Map<String, Int>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onSelected: (Language, Boolean) -> Unit,
    downloadLanguage: (Language) -> Unit,
    deleteLanguage: (Language) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    Column {
        Text(
            text = "Download language data/Select Language",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )

        SearchBar(
            searchQuery,
            modifier,
            onValueChange = { searchQuery = it },
        )

        val list = languageList.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(modifier = modifier.padding(top = 36.dp)) {
            items(
                items = list, key = { it.code }) { language ->
                val progress = downloadProgressMap[language.code] ?: language.downloadedProgress
                LanguageCard(
                    language = language.copy(downloadedProgress = progress),
                    downloadProgressMap,
                    onSelected,
                    downloadLanguage,
                    deleteLanguage
                )
            }
        }
    }

}

@Composable
fun LanguageCard(
    language: Language,
    downloadProgressMap: Map<String, Int>,
    onSelected: (Language, Boolean) -> Unit,
    downloadLanguage: (Language) -> Unit,
    deleteLanguage: (Language) -> Unit
) {
    //var isSelected by remember { mutableStateOf(language.isSelected) }
    //var isDownloaded by remember { mutableStateOf(language.isDownloaded) }
    var showDialog by remember { mutableStateOf(false) }
    var showDownloadProgressBar by remember { mutableStateOf(false) }
    val progress = downloadProgressMap[language.code] ?: language.downloadedProgress

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
        if (showDownloadProgressBar) {
            if (progress in 0..99) {
                ProgressBar(progress / 100f)
            } else {
                showDownloadProgressBar = false
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.name,
                modifier = Modifier
                    .weight(0.4f)
                    .padding(start = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = {
                    showDialog = true
                }) {
                var contentDescription = ""
                val icon = if (language.isDownloaded) {
                    contentDescription = "Delete the data"
                    painterResource(R.drawable.baseline_delete_24)
                } else {
                    contentDescription = "Download the data"
                    painterResource(
                        R.drawable.baseline_download_24
                    )
                }
                Icon(painter = icon, contentDescription = contentDescription)
            }
            Checkbox(
                checked = language.isSelected, modifier = Modifier.weight(0.2f), onCheckedChange = {
                    onSelected(language, it)
                })
        }
    }

    if (showDialog) {
        if (language.isDownloaded) {// delete data
            AlertDialog(
                title = { Text("Confirm Delete") },
                text = { Text("Do You want to delete the language data") },
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        deleteLanguage(language)

                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("Cancel")
                    }
                })
        } else {
            AlertDialog(//download data
                title = { Text("Confirm Download") },
                text = { Text("Do you want to download the language data?") },
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        downloadLanguage(language)
                        showDownloadProgressBar = true
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("No")
                    }
                })
        }
    }
}


@Composable
fun SearchBar(
    searchQuery: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .heightIn(min = 48.dp),
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(50.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = "search languages "
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.search))
        },
        onValueChange = { onValueChange(it) })
}


@Composable
fun ProgressBar(progress: Float) {
    Row {
        LinearProgressIndicator(progress = {
            progress
        })
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewDownloadLanguageDataScreen() {
    CharacherRecognizerTheme {
        val context = LocalContext.current
        val names = context.resources.getStringArray(R.array.ocr_engine_language_names)
        val keys = context.resources.getStringArray(R.array.ocr_engine_language_code)

        val items = keys.zip(names) { key, name ->
            Language(
                key, name, isDownloaded = Random.nextBoolean(), isSelected = Random.nextBoolean()
            )
        }

        DownloadLanguageDataScreenP(
            languageList = items,
            downloadProgressMap = mapOf("en" to 50, "fr" to 20),
            navController = rememberNavController(),
            onSelected = { code, value -> },
            downloadLanguage = { lang -> },
            deleteLanguage = { lang -> },
        )
    }
}