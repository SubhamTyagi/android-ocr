package io.github.subhamtyagi.ocr.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.subhamtyagi.ocr.R

enum class LanguageFilter {
    ALL, DOWNLOADED, SELECTED
}

@Composable
fun SummaryCard(
    downloadCount: Int,
    selectedLanguageCount: Int,
    activeFilter: LanguageFilter = LanguageFilter.ALL,
    onFilterChange: (LanguageFilter) -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.ocr_languages),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.download_language_data_and_choose_which_ones_ocr_should_use),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            StatsRow(downloadCount, selectedLanguageCount, activeFilter, onFilterChange)

        }
    }

}


@Composable
fun StatsRow(
    downloadCount: Int,
    selectedLanguageCount: Int,
    activeFilter: LanguageFilter,
    onFilterChange: (LanguageFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip(
            label = stringResource(R.string.language_data_downloaded),
            value = downloadCount,
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            active = activeFilter == LanguageFilter.DOWNLOADED,
            onClick = {
                val nextFilter =
                    if (activeFilter == LanguageFilter.DOWNLOADED) LanguageFilter.ALL else LanguageFilter.DOWNLOADED
                onFilterChange(nextFilter)
            },
            modifier = Modifier.weight(1f)
        )
        StatChip(
            label = stringResource(R.string.language_selected),
            value = selectedLanguageCount,
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            active = activeFilter == LanguageFilter.SELECTED,
            onClick = {
                val nextFilter =
                    if (activeFilter == LanguageFilter.SELECTED) LanguageFilter.ALL else LanguageFilter.SELECTED
                onFilterChange(nextFilter)
            },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun StatChip(
    label: String,
    value: Int,
    container: Color,
    content: Color,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = container,
        border = if (active) BorderStroke(2.dp, content) else null,
        tonalElevation = if (active) 8.dp else 0.dp
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = content
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = content.copy(alpha = 0.8f)
            )
        }
    }
}
