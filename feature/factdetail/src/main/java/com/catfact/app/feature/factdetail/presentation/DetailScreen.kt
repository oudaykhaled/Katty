package com.catfact.app.feature.factdetail.presentation

import androidx.compose.foundation.layout.Column
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onEvent: (DetailEvent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DetailEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    if (state.fact != null) {
                        IconButton(onClick = { onEvent(DetailEvent.ShareFact) }) {
                            Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.cd_share))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(MaterialTheme.spacing.extraLarge)
            )
        } else if (state.fact != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(MaterialTheme.spacing.extraLarge)
                    .verticalScroll(rememberScrollState())
                    .testTag("detail_screen")
            ) {
                Text(
                    text = state.fact.fact,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))

                CategoryChip(category = state.fact.category)

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                SyncIndicator(syncStatus = state.fact.syncStatus)

                Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))

                BookmarkButton(
                    isBookmarked = state.fact.isBookmarked,
                    onClick = { onEvent(DetailEvent.ToggleBookmark) }
                )

                Spacer(Modifier.height(MaterialTheme.spacing.xxxLarge))

                NoteEditor(
                    note = state.editedNote,
                    onNoteChange = { onEvent(DetailEvent.UpdateNote(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.hasNoteChanges) {
                    Spacer(Modifier.height(MaterialTheme.spacing.large))
                    Button(
                        onClick = { onEvent(DetailEvent.SaveNote) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.detail_save_note))
                    }
                }
            }
        } else if (state.error != null) {
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(MaterialTheme.spacing.extraLarge)
            )
        }
    }
}
