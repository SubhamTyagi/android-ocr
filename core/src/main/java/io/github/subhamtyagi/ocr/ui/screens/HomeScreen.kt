package io.github.subhamtyagi.ocr.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
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
import io.github.subhamtyagi.ocr.viewmodel.HomeViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val historyList by homeViewModel.history.collectAsStateWithLifecycle()
    val selectedLanguage by homeViewModel.selectedLanguages.collectAsStateWithLifecycle()
    val hasSettingsChanged by homeViewModel.hasSettingsChanged.collectAsStateWithLifecycle()
    val progress by homeViewModel.ocrProgress.collectAsStateWithLifecycle()
    val showOcrProgressBar by homeViewModel.isProcessing.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(hasSettingsChanged) {
        if (hasSettingsChanged) {
            homeViewModel.initOCR(context)
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let {
                homeViewModel.processImage(context, it)
            }
        }
    }

    HomeScreenP(historyList, selectedLanguage, cropImageLauncher, progress, showOcrProgressBar)
}


@Composable
fun HomeScreenP(
    historyList: List<History>,
    selectedLanguage: Set<Language>,
    cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>,
    progress: Int,
    showOcrProgressBar: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            MyFloatingActionButton(cropImageLauncher)
        }, modifier = modifier
    ) { paddingValues ->
        Column {
            val n = paddingValues
            if (showOcrProgressBar) {
                OcrProgressBar(progress)
            }
            DisplaySelectedLanguageName(selectedLanguage)
            if (!historyList.isEmpty()) HistoryOfOCRItems(historyList = historyList)
            else Text("No History")
        }
    }
}

@Composable
fun MyFloatingActionButton(cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>) {
    var pickImage by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = {
            pickImage = true
        }) {
        Icon(Icons.Filled.Add, contentDescription = "Scan New Image")
    }

    if (pickImage) {
        pickImage = false
        cropImageLauncher.launch(
            CropImageContractOptions(
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON
                ), uri = null
            )
        )
    }
}

@Composable
fun OcrProgressBar(progress: Int) {
    Log.d("HomeScreen", "ProgressBar: $progress")
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = {
                (progress * 1.5f / 100f).coerceIn(
                    0f,
                    1f
                )
            },//tesseract only show the progress % only till 67
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun HistoryOfOCRItems(
    historyList: List<History>
) {
    var selectedHistory by remember { mutableStateOf<History?>(null) }

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
                    text = "${historyList.size} OCRed Images${if (historyList.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No history available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList) { historyItem ->
                    HistoryItems(
                        historyItem,
                        onClick = {
                            selectedHistory = historyItem
                        },
                        onLongClick = { },
                        onDelete = { },
                        onFavorite = { },
                    )
                }
            }
        }
    }

    selectedHistory?.let {
        ShowBottomSheet(
            historyItem = it,
            dismiss = {
                selectedHistory = null
            }
        )
    }
}

@Composable
fun HistoryItems1(
    items: History,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
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

            Column(
                modifier = Modifier.weight(1f)
            ) {

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

                /*Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = items.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )*/
            }
        }
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
                    false // don't actually remove the composable here; let the caller update the list, which removes it from state
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onFavorite()
                    false // snap back after triggering the action, since this isn't a destructive swipe
                }

                SwipeToDismissBoxValue.Settled -> true
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        backgroundContent = {
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
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
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
        val list = arrayListOf<History>()
        repeat(5) {
            list.add(
                History(
                    title = "Title $it",
                    ocrText = " Text ${it * it}",
                    imagePath = "R.drawable.drawable_default_image_60"
                )
            )
        }
        var croppedImageUri by remember { mutableStateOf<Uri?>(null) }
        val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                croppedImageUri = result.uriContent
                var croppedBitmap = result.bitmap

            }
        }
        HomeScreenP(
            historyList = list, setOf(
                Language(
                    name = "English", code = "en", isDownloaded = true, isSelected = true
                ),
                Language(
                    name = "Latin", code = "lt", isDownloaded = false, isSelected = false
                ),
            ), cropImageLauncher,
            10,
            true
        )
    }
}