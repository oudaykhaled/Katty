package com.catfact.app.feature.favorites.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.component.ErrorBanner
import com.catfact.app.core.designsystem.component.FactCard
import com.catfact.app.core.designsystem.theme.sizing
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.favorites.R
import com.catfact.app.feature.favorites.viewmodel.FavoritesEvent
import com.catfact.app.feature.favorites.viewmodel.FavoritesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(state: FavoritesUiState, onEvent: (FavoritesEvent) -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.favorites_title)) }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            state.error?.let { error ->
                ErrorBanner(error, stringResource(R.string.cd_dismiss_error)) { onEvent(FavoritesEvent.DismissError) }
            }
            if (state.favorites.isNotEmpty()) FavoritesList(state.favorites, onEvent)
            else if (state.error == null) EmptyFavorites()
        }
    }
}

@Composable
private fun EmptyFavorites() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(R.string.cd_no_favorites),
            modifier = Modifier.size(MaterialTheme.sizing.iconLarge),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))
        Text(
            text = stringResource(R.string.favorites_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(R.string.favorites_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FavoritesList(favorites: List<CatFact>, onEvent: (FavoritesEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("favorites_list"),
        contentPadding = PaddingValues(MaterialTheme.spacing.extraLarge)
    ) {
        items(items = favorites, key = { it.id }, contentType = { "favorite" }) { fact ->
            FactCard(
                fact = fact,
                onBookmarkClick = { onEvent(FavoritesEvent.RemoveBookmark(fact.id)) },
                onClick = { onEvent(FavoritesEvent.FactClicked(fact.id)) },
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
            )
        }
    }
}
