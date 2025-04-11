package io.github.subhamtyagi.ocr.ui.composables

import androidx.annotation.PluralsRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun DropdownMenuPreference(
    title: String,
    options: List<String>,
    selectedOption: String,
    modifier: Modifier = Modifier,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, modifier, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = { expanded = true }) {
            Text(selectedOption, modifier, textAlign = TextAlign.Center)
        }
        DropdownMenu(
            expanded = expanded,/* modifier = modifier,*/
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
fun DropdownMenuPreference1(
    title: String,
    keyValueMap: Map<String, String>,
    options: List<String>,
    selectedOption: String,
    initialKey: String = keyValueMap.keys.first(),
    onSelected: (key: String, value: String) -> Unit
) {


}
@Composable
fun DropdownMenuPreferenceKeyValue(
    keyValueMap: Map<String, String>,
    label: String = "Select",
    initialKey: String = keyValueMap.keys.first(),
    onSelected: (key: String, value: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedKey by remember { mutableStateOf(initialKey) }

    Column {
        Text(text = "$label: $selectedKey -> ${keyValueMap[selectedKey]}", style = MaterialTheme.typography.bodyLarge)

        Box {
            Button(onClick = { expanded = true }) {
                Text(text = selectedKey)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                keyValueMap.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(text = key) },
                        onClick = {
                            selectedKey = key
                            expanded = false
                            onSelected(key, value)
                        }
                    )
                }
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
                    }) {
                Checkbox(checked = option in selectedOptions, onCheckedChange = null)
                Text(option)
            }
        }
    }
}

@Composable
fun SwitchPreference(
    title: String, summary: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCheckedChange(!checked) }) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )

        }
        Switch(checked = checked, onCheckedChange = { onCheckedChange(it) })
    }
}

@Composable
fun EditTextPreference(
    title: String, summary: String, value: String, onValueChange: (String) -> Unit
) {
    var dialogOpen by remember { mutableStateOf(false) }
    Column {
        PreferenceItem(
            title = title, summary = summary, value = value, onClick = { dialogOpen = true })

        if (dialogOpen) {
            var text by remember { mutableStateOf(value) }
            AlertDialog(
                onDismissRequest = { dialogOpen = false },
                title = { Text(text = title) },
                text = {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Enter Value") })
                },
                confirmButton = {
                    TextButton(onClick = {
                        onValueChange(text)
                        dialogOpen = false
                    }) { Text("Ok") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        dialogOpen = false
                    }) { Text("Cancel") }
                })
        }
    }
}

@Composable
private fun PreferenceItem(title: String, summary: String, value: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = summary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)

    }
}

@Preview
@Composable
fun DropdownMenuPreferenceKeyValuePreview() {
    val options = mapOf(
        "Language" to "English",
        "Theme" to "Dark Mode",
        "Font Size" to "Medium"
    )

    DropdownMenuPreferenceKeyValue(
        keyValueMap = options,
        label = "Choose Option",
        onSelected = { key, value ->

        }
    )
}
