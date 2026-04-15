package com.catfact.app.feature.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.ErrorKind
import com.catfact.app.core.domain.model.toErrorKind
import com.catfact.app.core.domain.usecase.GetFavoritesUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.telemetry.EventTracker
import com.catfact.app.feature.favorites.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val eventTracker: EventTracker
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private val _sideEffects = Channel<FavoritesSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        eventTracker.trackScreenView("FavoritesScreen")
        observeFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveBookmark -> removeBookmark(event.factId)
            is FavoritesEvent.DismissError -> _state.update { it.copy(error = null) }
            is FavoritesEvent.FactClicked -> viewModelScope.launch {
                _sideEffects.send(FavoritesSideEffect.NavigateToDetail(event.factId))
            }
        }
    }

    private fun observeFavorites() {
        getFavoritesUseCase()
            .distinctUntilChanged()
            .map<List<CatFact>, ImmutableList<CatFact>> { it.toImmutableList() }
            .onEach { favorites ->
                _state.update { it.copy(favorites = favorites, isLoading = false) }
            }
            .catch { e -> _state.update { it.copy(error = e.toUiText(), isLoading = false) } }
            .launchIn(viewModelScope)
    }

    private fun Throwable.toUiText(): UiText = when (toErrorKind()) {
        is ErrorKind.Network -> UiText.Resource(R.string.error_network)
        is ErrorKind.Server -> UiText.Resource(R.string.error_server)
        is ErrorKind.Unknown -> UiText.Resource(R.string.error_load_favorites_failed)
    }

    private fun removeBookmark(factId: String) {
        viewModelScope.launch {
            try {
                toggleBookmarkUseCase(factId, isCurrentlyBookmarked = true)
            } catch (e: Exception) {
                _sideEffects.send(FavoritesSideEffect.ShowSnackbar(UiText.Resource(R.string.error_remove_bookmark_failed)))
            }
        }
    }
}
