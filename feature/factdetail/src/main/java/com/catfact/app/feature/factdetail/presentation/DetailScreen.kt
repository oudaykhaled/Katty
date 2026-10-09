package com.catfact.app.feature.factdetail.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.component.BookmarkButton
import com.catfact.app.core.designsystem.component.CategoryChip
import com.catfact.app.core.designsystem.component.SyncIndicator
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.designsystem.util.asString
import com.catfact.app.feature.factdetail.R
import com.catfact.app.feature.factdetail.presentation.components.NoteEditor
import com.catfact.app.feature.factdetail.viewmodel.DetailEvent
import com.catfact.app.feature.factdetail.viewmodel.DetailUiState

@Composable
fun DetailScreen(state: DetailUiState, onEvent: (DetailEvent) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { DetailTopBar(state, onEvent) }
    ) { padding ->
        when {
            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(padding).padding(MaterialTheme.spacing.extraLarge)
            )
            state.fact != null -> DetailContent(state, onEvent, Modifier.padding(padding))
            state.error != null -> Text(
                text = state.error.asString(), color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxSize().padding(padding).padding(MaterialTheme.spacing.extraLarge)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(state: DetailUiState, onEvent: (DetailEvent) -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.detail_title)) },
        navigationIcon = {
            IconButton(
                onClick = { onEvent(DetailEvent.NavigateBack) }, modifier = Modifier.testTag("detail_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back))
            }
        },
        actions = {
            if (state.fact != null) {
                IconButton(
                    onClick = { onEvent(DetailEvent.ShareFact) }, modifier = Modifier.testTag("detail_share")
                ) {
                    Icon(Icons.Filled.Share, stringResource(R.string.cd_share))
                }
            }
        }
    )
}

@Composable
private fun DetailContent(state: DetailUiState, onEvent: (DetailEvent) -> Unit, modifier: Modifier = Modifier) {
    val fact = state.fact ?: return
    Column(
        modifier = modifier.fillMaxSize().padding(MaterialTheme.spacing.extraLarge)
            .verticalScroll(rememberScrollState()).testTag("detail_screen")
    ) {
        Text(
            text = fact.fact, style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("detail_fact_text")
        )
        Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))
        CategoryChip(category = fact.category)
        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        SyncIndicator(syncStatus = fact.syncStatus)
        Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))
        BookmarkButton(
            isBookmarked = fact.isBookmarked,
            onClick = { onEvent(DetailEvent.ToggleBookmark) }, modifier = Modifier.testTag("detail_bookmark")
        )
        Spacer(Modifier.height(MaterialTheme.spacing.xxxLarge))
        NoteSection(state, onEvent)
    }
}

@Composable
private fun ColumnScope.NoteSection(state: DetailUiState, onEvent: (DetailEvent) -> Unit) {
    NoteEditor(
        note = state.editedNote,
        onNoteChange = { onEvent(DetailEvent.UpdateNote(it)) }, modifier = Modifier.fillMaxWidth()
    )
    if (state.hasNoteChanges) {
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        Button(
            onClick = { onEvent(DetailEvent.SaveNote) }, enabled = !state.isSavingNote,
            modifier = Modifier.align(Alignment.End).testTag("detail_save_note")
        ) { Text(stringResource(R.string.detail_save_note)) }
    }
}
