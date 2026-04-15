package com.catfact.app.feature.facts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.ErrorKind
import com.catfact.app.core.domain.model.toErrorKind
import com.catfact.app.core.domain.usecase.FetchRandomFactUseCase
import com.catfact.app.core.domain.usecase.GetFactsUseCase
import com.catfact.app.core.domain.usecase.RefreshFactsUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.telemetry.EventTracker
import com.catfact.app.feature.facts.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FactsViewModel @Inject constructor(
    private val getFactsUseCase: GetFactsUseCase,
    private val fetchRandomFactUseCase: FetchRandomFactUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val refreshFactsUseCase: RefreshFactsUseCase,
    private val eventTracker: EventTracker
) : ViewModel() {

    private val _state = MutableStateFlow(FactsUiState())
    val state: StateFlow<FactsUiState> = _state.asStateFlow()

    private val _sideEffects = Channel<FactsSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        eventTracker.trackScreenView("FactsScreen")
        observeFacts()
        setupSearchDebounce()
        loadInitial()
    }

    fun onEvent(event: FactsEvent) {
        when (event) {
            is FactsEvent.Refresh -> refresh()
            is FactsEvent.LoadNextPage -> loadNextPage()
            is FactsEvent.ToggleBookmark -> toggleBookmark(event.factId, event.isCurrentlyBookmarked)
            is FactsEvent.Search -> onSearch(event.query)
            is FactsEvent.DismissError -> _state.update { it.copy(error = null) }
            is FactsEvent.FactClicked -> viewModelScope.launch {
                _sideEffects.send(FactsSideEffect.NavigateToDetail(event.factId))
            }
            is FactsEvent.FetchRandomFact -> fetchRandomFact()
        }
    }

    private fun observeFacts() {
        getFactsUseCase()
            .distinctUntilChanged()
            .map<List<CatFact>, ImmutableList<CatFact>> { it.toImmutableList() }
            .onEach { facts -> _state.update { it.copy(facts = facts) } }
            .catch { e -> _state.update { it.copy(error = e.toUiText()) } }
            .launchIn(viewModelScope)
    }

    private fun setupSearchDebounce() {
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .map { it }
            .onEach { query -> _state.update { it.copy(searchQuery = query) } }
            .launchIn(viewModelScope)
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                fetchRandomFactUseCase().let { fact ->
                    _state.update { it.copy(randomFact = fact) }
                }
            } catch (_: Exception) { /* Random fact is optional */ }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                fetchRandomFactUseCase().let { fact ->
                    _state.update { it.copy(randomFact = fact) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.toUiText()) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMorePages) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                val nextPage = current.currentPage + 1
                val result = refreshFactsUseCase(nextPage, PAGE_SIZE)
                _state.update {
                    it.copy(currentPage = nextPage, hasMorePages = result.hasMorePages)
                }
            } catch (e: Exception) {
                _sideEffects.send(FactsSideEffect.ShowSnackbar(UiText.Resource(R.string.error_load_failed)))
            }
            _state.update { it.copy(isLoadingMore = false) }
        }
    }

    private fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    bookmarkLoadingIds = (state.bookmarkLoadingIds + factId).toImmutableList()
                )
            }
            try {
                toggleBookmarkUseCase(factId, isCurrentlyBookmarked)
                eventTracker.trackBookmarkToggled(factId, !isCurrentlyBookmarked)
            } catch (e: Exception) {
                _sideEffects.send(FactsSideEffect.ShowSnackbar(UiText.Resource(R.string.error_bookmark_failed)))
            }
            _state.update { state ->
                state.copy(
                    bookmarkLoadingIds = state.bookmarkLoadingIds.filter { it != factId }.toImmutableList()
                )
            }
        }
    }

    private fun onSearch(query: String) {
        _state.update { it.copy(searchInput = query) }
        searchQueryFlow.value = query
    }

    private fun fetchRandomFact() {
        viewModelScope.launch {
            try {
                val fact = fetchRandomFactUseCase()
                _state.update { it.copy(randomFact = fact) }
            } catch (e: Exception) {
                _sideEffects.send(FactsSideEffect.ShowSnackbar(UiText.Resource(R.string.error_fetch_failed)))
            }
        }
    }

    private fun Throwable.toUiText(): UiText = when (toErrorKind()) {
        is ErrorKind.Network -> UiText.Resource(R.string.error_network)
        is ErrorKind.Server -> UiText.Resource(R.string.error_server)
        is ErrorKind.Unknown -> UiText.Resource(R.string.error_load_failed)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val PAGE_SIZE = 20
    }
}
