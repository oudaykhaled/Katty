package com.catfact.app.feature.favorites.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catfact.app.core.designsystem.util.resolve
import com.catfact.app.feature.favorites.viewmodel.FavoritesSideEffect
import com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel

@Composable
fun FavoritesRoute(
    onNavigateToDetail: (String) -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnNavigate by rememberUpdatedState(onNavigateToDetail)
    val currentOnSnackbar by rememberUpdatedState(onShowSnackbar)
    val resources = LocalResources.current
    val currentResources by rememberUpdatedState(resources)

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is FavoritesSideEffect.NavigateToDetail -> currentOnNavigate(effect.factId)
                is FavoritesSideEffect.ShowSnackbar -> {
                    currentOnSnackbar(effect.message.resolve(currentResources))
                }
            }
        }
    }

    FavoritesScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
