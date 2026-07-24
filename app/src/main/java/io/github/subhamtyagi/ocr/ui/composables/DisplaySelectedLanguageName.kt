package io.github.subhamtyagi.ocr.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme
import kotlin.collections.forEach

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplaySelectedLanguageName(
    selectedLanguage: Set<Language>
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.selected_languages),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedLanguage.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_language_selected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    selectedLanguage.forEach { language ->
                        AssistChip(
                            onClick = {},
                            enabled = false,
                            label = {
                                Text(language.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDisplaySelectedLanguageName() {
    CharacherRecognizerTheme {

        DisplaySelectedLanguageName(
            setOf(
                Language(
                    name = "English", code = "en", isDownloaded = true, isSelected = true
                ),
                Language(
                    name = "English old ", code = "end", isDownloaded = true, isSelected = true
                ),
                Language(
                    name = "Latin", code = "lt", isDownloaded = false, isSelected = false
                ),
                Language(
                    name = "Hindi", code = "hin", isDownloaded = true, isSelected = true
                ),
                Language(
                    name = "German", code = "dut", isDownloaded = true, isSelected = true
                ),
            )
        )
    }
}
