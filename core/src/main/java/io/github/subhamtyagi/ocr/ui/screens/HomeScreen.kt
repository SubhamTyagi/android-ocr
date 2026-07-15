package io.github.subhamtyagi.ocr.ui.screens

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.room.History
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.ui.composables.ShowBottomSheet
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import io.github.subhamtyagi.ocr.viewmodel.HomeViewModel
import java.io.File

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val historyList by homeViewModel.history.collectAsState()
    val selectedLanguage by homeViewModel.selectedLanguages.collectAsState()
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }

    val hasSettingsChanged by homeViewModel.hasSettingsChanged.collectAsState()

    var context = LocalContext.current

    if (hasSettingsChanged) {
        homeViewModel.initOCR(context) { progress ->
            //TODO: progress update
            // Log.d(TAG, "HomeScreen: $progress")
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            croppedImageUri = result.uriContent
            croppedImageUri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                homeViewModel.getTextFromBitmap(bitmap = bitmap) { text ->
                    homeViewModel.saveBitmapToStorage(context = context, bitmap = bitmap) { file ->
                        homeViewModel.addHistory(
                            History(
                                title = "Ocr Text",
                                ocrText = text,
                                imagePath = file.absolutePath
                            )
                        )
                    }
                }
            }
        }
    }
    HomeScreenP(historyList, selectedLanguage, cropImageLauncher)
}


@Composable
fun HomeScreenP(
    historyList: List<History>,
    selectedLanguage: Set<Language>,
    cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            MyFloatingActionButton(cropImageLauncher)
        }, modifier = modifier
    ) { paddingValues ->
        Column {
            var n = paddingValues
            ProgressBar()
            DisplayLanguageName(selectedLanguage)
            if (!historyList.isEmpty()) HistoryOfOCRItems(historyList = historyList)
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
        Icon(Icons.Filled.Add, contentDescription = "Add")
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
private fun DisplayLanguageName(selectedLanguage: Set<Language>) {
    Row {
        Text(
            text = stringResource(R.string.selected_languages),
            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )
        Text(
            text = selectedLanguage.joinToString(", ") { it.name },
            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        )
    }
}

@Composable
fun HistoryOfOCRItems(historyList: List<History>) {
    Text(
        text = "History",
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
                historyItem,
                onClick = { showOcrResult = true })
            if (showOcrResult) {
                ShowBottomSheet(historyItem, dismiss = { showOcrResult = false })
            }
        }

    }
}

@Composable
fun HistoryItems(items: History, onClick: () -> Unit) {
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
                painter = rememberAsyncImagePainter(items.imagePath),
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
                    text = items.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = items.ocrText, modifier = Modifier
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
        val list = arrayListOf<History>()
        repeat(5) {
            list.add(
                History(
                    title = "Title $it",
                    ocrText = " Text",
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
            ), cropImageLauncher
        )
    }
}