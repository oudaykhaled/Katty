package com.catfact.app.feature.factdetail.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.catfact.app.feature.factdetail.R

@Composable
fun NoteEditor(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_editor"),
        label = { Text(stringResource(R.string.detail_note_label)) },
        placeholder = { Text(stringResource(R.string.detail_note_placeholder)) },
        minLines = 3,
        maxLines = 6
    )
}
