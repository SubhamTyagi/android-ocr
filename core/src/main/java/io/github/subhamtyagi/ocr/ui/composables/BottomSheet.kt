package io.github.subhamtyagi.ocr.ui.composables

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil.compose.rememberAsyncImagePainter
import io.github.subhamtyagi.ocr.data.room.History

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheet(
    historyItem: History,
    dismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val expanded = sheetState.currentValue == SheetValue.Expanded

    val imageHeight by animateDpAsState(
        targetValue = when (sheetState.targetValue) {
            SheetValue.Expanded -> 96.dp
            SheetValue.PartiallyExpanded -> 220.dp
            SheetValue.Hidden -> 220.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ImageHeight"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 12.dp else 24.dp,
        animationSpec = tween(350),
        label = ""
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.8f else 1f,
        animationSpec = tween(350),
        label = ""
    )

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            Image(
                painter = rememberAsyncImagePainter(historyItem.imagePath),
                contentDescription = historyItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(cornerRadius))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = historyItem.title,
                modifier = Modifier.alpha(titleAlpha),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilledTonalIconButton(
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(historyItem.ocrText)
                        )

                        Toast.makeText(
                            context,
                            "Copied to clipboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy"
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, historyItem.ocrText)
                            type = "text/plain"
                        }

                        context.startActivity(
                            Intent.createChooser(sendIntent, "Share OCR text")
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recognized Text",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 350.dp)
            ) {
                Text(
                    text = historyItem.ocrText,
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
