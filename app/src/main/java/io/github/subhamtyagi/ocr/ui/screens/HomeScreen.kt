package io.github.subhamtyagi.ocr.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.data.room.History
import io.github.subhamtyagi.ocr.ui.composables.DisplaySelectedLanguageName
import io.github.subhamtyagi.ocr.ui.composables.ShowBottomSheet
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.HomeUiState
import io.github.subhamtyagi.ocr.viewmodel.HomeViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedImageUri: Uri? = null,
    onSharedImageHandled: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            homeViewModel.clearError()
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                if (uiState.showLanguageDialog) {
                    pendingImageUri = uri
                    showLanguageDialog = true
                } else {
                    homeViewModel.processImage(uri)
                }
            }
        }
    }

    LaunchedEffect(sharedImageUri) {
        sharedImageUri?.let { uri ->
            cropImageLauncher.launch(
                CropImageContractOptions(
                    cropImageOptions = CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON
                    ), uri = uri
                )
            )
            onSharedImageHandled()
        }
    }

    if (showLanguageDialog && pendingImageUri != null) {
        LanguageSelectionDialog(
            availableLanguages = uiState.selectedLanguages,
            onDismiss = {
                showLanguageDialog = false
                pendingImageUri = null
            },
            onConfirm = { selected ->
                pendingImageUri?.let { uri ->
                    homeViewModel.processImage(uri, selected)
                }
                showLanguageDialog = false
                pendingImageUri = null
            }
        )
    }

    HomeScreenContent(
        uiState = uiState, snackbarHostState = snackbarHostState, onScanImage = {
        cropImageLauncher.launch(
            CropImageContractOptions(
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON
                ), uri = null
            )
        )
    }, onDeleteHistory = { homeViewModel.deleteHistory(it) }, modifier = modifier
    )
}


@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onScanImage: () -> Unit,
    onDeleteHistory: (History) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
        FloatingActionButton(onClick = onScanImage) {
            Icon(Icons.Filled.Add, contentDescription = "Scan New Image")
        }
    }, modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isProcessing) {
                OcrProgressBar(uiState.ocrProgress)
            }
            DisplaySelectedLanguageName(uiState.selectedLanguages)
            if (uiState.history.isNotEmpty()) {
                HistoryOfOCRItems(
                    historyList = uiState.history, onDelete = onDeleteHistory
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No History", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguageSelectionDialog(
    availableLanguages: Set<Language>,
    onDismiss: () -> Unit,
    onConfirm: (Set<Language>) -> Unit
) {
    var selectedLanguages by remember { mutableStateOf(availableLanguages) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Languages for Scan") },
        text = {
            Column {
                Text(
                    "Choose the languages present in the image for better accuracy.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableLanguages.forEach { language ->
                        FilterChip(
                            selected = language in selectedLanguages,
                            onClick = {
                                selectedLanguages = if (language in selectedLanguages) {
                                    selectedLanguages - language
                                } else {
                                    selectedLanguages + language
                                }
                            },
                            label = { Text(language.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedLanguages) },
                enabled = selectedLanguages.isNotEmpty()
            ) {
                Text("Start OCR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OcrProgressBar(progress: Int) {
    Log.d("HomeScreen", "ProgressBar: $progress")
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = {
                (progress * 1.5f / 100f).coerceIn(0f, 1f)
            },//tesseract only show the progress % only till 67
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun HistoryOfOCRItems(
    historyList: List<History>, onDelete: (History) -> Unit
) {
    var selectedHistory by remember { mutableStateOf<History?>(null) }
    val listState = rememberLazyListState()

    val firstItemId = remember(historyList) { historyList.firstOrNull()?.id }
    LaunchedEffect(firstItemId) {
        if (historyList.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${historyList.size} OCRed Image${if (historyList.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(
                horizontal = 16.dp, vertical = 8.dp
            ), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyList, key = { it.id ?: it.imagePath }) { historyItem ->
                HistoryItems(
                    historyItem,
                    onClick = {
                        selectedHistory = historyItem
                    },
                    onLongClick = { },
                    onDelete = { onDelete(historyItem) },
                    onFavorite = { },
                )
            }
        }
    }

    selectedHistory?.let {
        ShowBottomSheet(
            historyItem = it, dismiss = {
                selectedHistory = null
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryItems(
    items: History,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onFavorite()
                    false
                }

                SwipeToDismissBoxValue.Settled -> true
            }
        })

    SwipeToDismissBox(
        state = dismissState, modifier = modifier.fillMaxWidth(), backgroundContent = {
            val (color, icon, alignment) = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Triple(
                    Color(0xFFFFC107), Icons.Default.Star, Alignment.CenterStart
                )

                SwipeToDismissBoxValue.EndToStart -> Triple(
                    MaterialTheme.colorScheme.error, Icons.Default.Delete, Alignment.CenterEnd
                )

                else -> Triple(Color.Transparent, null, Alignment.Center)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .padding(horizontal = 24.dp), contentAlignment = alignment
            ) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = Color.White)
                }
            }
        }) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick, onLongClick = onLongClick
                ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(items.imagePath),
                    contentDescription = items.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = items.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = items.ocrText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CharacherRecognizerTheme {
        val list = List(5) {
            History(
                id = it,
                title = "Accuracy: ${80 + it}%",
                ocrText = " Text ${it * it}",
                imagePath = "R.drawable.drawable_default_image_60",
                accuracy = 80 + it
            )
        }
        HomeScreenContent(
            uiState = HomeUiState(
            history = list, selectedLanguages = setOf(
                Language(name = "English", code = "en", isDownloaded = true, isSelected = true)
            ), ocrProgress = 10, isProcessing = true
        ),
            snackbarHostState = remember { SnackbarHostState() },
            onScanImage = {},
            onDeleteHistory = {})
    }
}
