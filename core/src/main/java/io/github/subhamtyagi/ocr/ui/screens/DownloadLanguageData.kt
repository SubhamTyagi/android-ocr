package io.github.subhamtyagi.ocr.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.DownloadLanguageViewModel
import kotlin.random.Random

//Check item is not working yet
@Composable
fun DownloadLanguageDataScreen(
    downloadViewModel: DownloadLanguageViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val selectedLanguages = downloadViewModel.selectedLanguages.collectAsState()
    val languageList: List<Language> = downloadViewModel.languageList
    languageList.forEach {
        it.isSelected = selectedLanguages.value.contains(it.code)
    }
    DownloadLanguageDataScreenP(
        languageList = languageList,
        navController = navController,
        onSelected = { code, value ->
            if (value) {
                selectedLanguages.value.plus(code)
            } else {
                selectedLanguages.value.minus(code)
            }
            downloadViewModel.updateSelectedLanguages(selectedLanguages.value)
        },
        downloadLanguage = { lang -> downloadViewModel.downloadLanguage(lang) },
        deleteLanguage = { lang -> downloadViewModel.deleteLanguage(lang) })

}

@Composable
fun DownloadLanguageDataScreenP(
    languageList: List<Language>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onSelected: (String, Boolean) -> Unit,
    downloadLanguage: (String) -> Unit,
    deleteLanguage: (String) -> Unit
) {
    Column {
        Text(
            text = "Download language data/Select Language",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )

        var searchQuery by remember { mutableStateOf("") }
        val list = languageList.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }.toMutableStateList()

        SearchBar(
            searchQuery,
            modifier,
            onValueChange = { searchQuery = it },
        )
        LazyColumn(modifier = modifier.padding(top = 36.dp)) {
            items(list) { language ->
                LanguageCard(
                    language, onSelected, downloadLanguage, deleteLanguage
                )
            }
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
fun LanguageCard(
    language: Language,
    onSelected: (String, Boolean) -> Unit,
    downloadLanguage: (String) -> Unit,
    deleteLanguage: (String) -> Unit
) {
    var isSelected by remember { mutableStateOf(language.isSelected) }
    var isDownloaded by remember { mutableStateOf(language.isDownloaded) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
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
                },

                ) {
                val icon =
                    if (isDownloaded) painterResource(R.drawable.baseline_delete_24) else painterResource(
                        R.drawable.baseline_download_24
                    )
                val contentDescription =
                    if (isDownloaded) "Delete the data" else "Download the data"
                Icon(painter = icon, contentDescription = contentDescription)
            }
            Checkbox(
                isSelected, modifier = Modifier.weight(0.2f), onCheckedChange = {
                    isSelected = it
                    language.isSelected = isSelected
                    onSelected(language.code, isSelected)
                })
        }
    }

    if (showDialog) {
        if (isDownloaded) {
            AlertDialog(
                title = { Text("Confirm Delete") },
                text = { Text("Do You want to delete the language data") },
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        deleteLanguage(language.code)
                        isDownloaded = false
                        language.isDownloaded = false
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
            AlertDialog(
                title = { Text("Confirm Download") },
                text = { Text("Do you want to download the language data?") },
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        //Todo: download the data and show progress bar
                        downloadLanguage(language.code)
                        isDownloaded = true
                        language.isDownloaded = true
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
private fun ProgressBar(show: Boolean) {
    val showDownloadProgressBar by remember { mutableStateOf(show) }
    if (showDownloadProgressBar) {
        Row {
            LinearProgressIndicator(progress = {
                15f
            })
        }
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
            navController = rememberNavController(),
            onSelected = { code, value -> },
            downloadLanguage = { lang -> },
            deleteLanguage = { lang -> })
    }
}