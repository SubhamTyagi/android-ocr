package io.github.subhamtyagi.ocr.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import io.github.subhamtyagi.ocr.ui.screens.SettingsScreen
import io.github.subhamtyagi.ocr.ui.theme.CharacherRecognizerTheme

@Composable
fun DropdownMenuPreference(
    title: String,
    options: List<String>,
    selectedOption: String,
    modifier: Modifier = Modifier,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Text(title,modifier, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = { expanded = true }) {
            Text(selectedOption,modifier, textAlign = TextAlign.Center)
        }
        DropdownMenu(
            expanded = expanded,
           /* modifier = modifier,*/
            onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun MultiSelectPreference(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newSelection =
                            if (option in selectedOptions) selectedOptions - option else selectedOptions + option
                        onSelectionChange(newSelection)
                    }
            ) {
                Checkbox(checked = option in selectedOptions, onCheckedChange = null)
                Text(option)
            }
        }
    }
}

@Composable
fun SwitchPreference(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { onCheckedChange(it) })
    }
}


@Preview(showBackground = true)
@Composable
fun DropdownPreview() {
    CharacherRecognizerTheme {
        DropdownMenuPreference(
            title = "Select Tesseract Data Type",
            options = listOf("best", "fast", "standard"),
            selectedOption ="best" ,
            modifier = Modifier.fillMaxWidth(),
            onOptionSelected = { }

        )
    }
}