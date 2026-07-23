package io.github.subhamtyagi.ocr.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.downloader.DownloadResult
import io.github.subhamtyagi.ocr.downloader.DownloadResult.Failure
import io.github.subhamtyagi.ocr.downloader.DownloadResult.Success
import io.github.subhamtyagi.ocr.ui.composables.SearchBar
import io.github.subhamtyagi.ocr.ui.composables.StatusBadge
import io.github.subhamtyagi.ocr.ui.composables.SummaryCard
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.DownloadLanguageViewModel
import kotlin.random.Random

@Composable
fun DownloadLanguageDataScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadLanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
   // val downloadedLanguages by downloadViewModel.downloadedLanguages.collectAsStateWithLifecycle()
    val selectedLanguages by downloadViewModel.selectedLanguages.collectAsStateWithLifecycle()
    val progressMap by downloadViewModel.downloadProgressMap.collectAsStateWithLifecycle()
    val downloadResultFlow = downloadViewModel.downloadResultFlow

    val selectedCodes = remember(selectedLanguages) {
        selectedLanguages.map { it.code }.toSet()
    }
    val languageList = downloadViewModel.getLanguagesList(selectedLanguages)

    LaunchedEffect(Unit) {
        downloadViewModel.observeTessDirectory(languageList)
    }

    LaunchedEffect(downloadResultFlow) {
        downloadResultFlow.collect { result ->
            when (result) {
                is Success -> {
                   // downloadViewModel.observeTessDirectory(languageList)
                    Toast.makeText(
                        context,
                        "Downloaded ${result.language.name} successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Failure -> {
                    Toast.makeText(
                        context,
                        "Failed to download ${result.language.name}: ${result.reason}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    DownloadLanguageContent(
        modifier = modifier,
        languageList = languageList,
        selectedCodes = selectedCodes,
        downloadProgressMap = progressMap,
        onSelected = { language, value ->
            downloadViewModel.updateSelectedLanguages(language, value)
        },
        downloadLanguage = { lang -> downloadViewModel.downloadLanguage(lang) },
        deleteLanguage = { lang -> downloadViewModel.deleteLanguage(lang) }
    )
}

@Composable
fun DownloadLanguageContent(
    languageList: List<Language>,
    selectedCodes: Set<String>,
    downloadProgressMap: Map<String, Int>,
    modifier: Modifier = Modifier,
    onSelected: (Language, Boolean) -> Unit,
    downloadLanguage: (Language) -> Unit,
    deleteLanguage: (Language) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList =
        languageList.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }


    Column(modifier = modifier) {
        SummaryCard(languageList.count { it.isDownloaded }, selectedCodes.size)
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(
            searchQuery,
            onValueChange = { searchQuery = it },
        )
        if (filteredList.isEmpty()) {
            EmptySearchState(searchQuery)
        } else {
            LazyColumn(modifier = Modifier.padding(top = 18.dp)) {
                items(
                    items = filteredList,
                    key = { it.code }
                ) { language ->
                    LanguageCard(
                        language = language,
                        selectedCodes = selectedCodes,
                        downloadProgressMap = downloadProgressMap,
                        onSelected = onSelected,
                        downloadLanguage = downloadLanguage,
                        deleteLanguage = deleteLanguage
                    )
                }
            }
        }
    }
}


@Composable
fun LanguageCard(
    language: Language,
    selectedCodes: Set<String>,
    downloadProgressMap: Map<String, Int>,
    onSelected: (Language, Boolean) -> Unit,
    downloadLanguage: (Language) -> Unit,
    deleteLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val progress = downloadProgressMap[language.code] ?: language.downloadedProgress
    val isDownloading = downloadProgressMap.containsKey(language.code)

    val listItemColor = if (language.isDownloaded) {
        MaterialTheme.colorScheme.primaryContainer
            .copy(alpha = 0.35f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        MaterialTheme.colorScheme.surface
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = listItemColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(color = listItemColor),

            ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            showDialog = true
                        }) {

                        Icon(
                            painter = painterResource(
                                if (language.isDownloaded) R.drawable.baseline_delete_24
                                else R.drawable.baseline_download_24
                            ), contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = language.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(2.dp))

                    StatusBadge(
                        downloading = isDownloading, downloaded = language.isDownloaded
                    )

                }
                Checkbox(
                    checked = language.code in selectedCodes,
                    enabled = language.isDownloaded or (language.code in selectedCodes),
                    onCheckedChange = {
                        onSelected(language, it)
                    }
                )
            }

            AnimatedVisibility(
                visible = isDownloading
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$progress%",
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }
    }

    if (showDialog) {
        if (language.isDownloaded) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, icon = {
                Icon(
                    Icons.Filled.Delete, contentDescription = null
                )
            },
                title = { Text("Delete ${language.name}?") },
                text = { Text("This will remove the downloaded OCR language data from your device.") },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            deleteLanguage(language)
                            showDialog = false
                        }) {
                        Text("Delete")
                    }
                }, dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }) {
                        Text("Cancel")
                    }
                })
        } else {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                icon = {
                    Icon(
                        Icons.Filled.Download, contentDescription = null
                    )
                },
                title = { Text("Download ${language.name}?") },
                text = { Text("The OCR language data will be downloaded and stored locally.") },
                confirmButton = {
                    FilledTonalButton(
                        onClick = {
                            downloadLanguage(language)
                            showDialog = false
                        }) {
                        Text("Download")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }) {
                        Text("Cancel")
                    }
                })
        }
    }
}

@Composable
fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No languages match \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDownloadLanguageDataScreen() {
    CharacherRecognizerTheme {
        val names = stringArrayResource(R.array.ocr_engine_language_names)
        val keys = stringArrayResource(R.array.ocr_engine_language_code)

        val items = keys.zip(names) { key, name ->
            Language(
                key, name, isDownloaded = Random.nextBoolean(), isSelected = Random.nextBoolean()
            )
        }

        DownloadLanguageContent(
            languageList = items,
            selectedCodes = setOf("en", "fr"),
            downloadProgressMap = mapOf("en" to 50, "fr" to 20),
            onSelected = { _, _ -> },
            downloadLanguage = { _ -> },
            deleteLanguage = { _ -> },
        )
    }
}
