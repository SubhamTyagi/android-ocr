package io.github.subhamtyagi.ocr.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.subhamtyagi.ocr.data.model.History
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheet(historyItem: History, dismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = dismiss, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionBar(modifier = Modifier.fillMaxWidth())
            TextContent(
                historyItem.ocrText,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        dismiss()
                    }
                }
            }) {
                Text("Hide Bottom Sheet")
            }

        }
    }

}

@Composable
fun TextContent(ocrText: String, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Text(
        text = ocrText,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Start),
        onTextLayout = { textLayoutResult ->

        })
}

@Composable
fun ActionBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(55.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { /* Handle copy action */ }, modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.CopyAll, contentDescription = "Copy"
            )
        }
        IconButton(
            onClick = { /* Handle share action */ }, modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Share, contentDescription = "share"
            )
        }
    }
}