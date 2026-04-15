package com.catfact.app.feature.factdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.ErrorKind
import com.catfact.app.core.domain.model.toErrorKind
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.domain.usecase.SaveNoteUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.telemetry.EventTracker
import com.catfact.app.feature.factdetail.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Assisted private val factId: String,
    private val repository: CatFactRepository,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val eventTracker: EventTracker
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(factId: String): DetailViewModel
    }

    private val _state = MutableStateFlow(
        DetailUiState(editedNote = savedStateHandle.get<String>(DRAFT_NOTE_KEY) ?: "")
    )
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    private val _sideEffects = Channel<DetailSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        loadFact()
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.ToggleBookmark -> toggleBookmark()
            is DetailEvent.UpdateNote -> updateNote(event.note)
            is DetailEvent.SaveNote -> saveNote()
            is DetailEvent.ShareFact -> shareFact()
            is DetailEvent.NavigateBack -> viewModelScope.launch {
                _sideEffects.send(DetailSideEffect.NavigateBack)
            }
        }
    }

    private fun loadFact() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val fact = repository.getFactById(factId)
                if (fact != null) {
                    eventTracker.trackFactViewed(factId, fact.category.name)
                    val draftNote = savedStateHandle.get<String>(DRAFT_NOTE_KEY)
                    _state.update {
                        it.copy(
                            fact = fact,
                            editedNote = draftNote ?: fact.personalNote,
                            isLoading = false,
                            hasNoteChanges = draftNote != null && draftNote != fact.personalNote
                        )
                    }
                } else {
                    _state.update { it.copy(error = UiText.Resource(R.string.error_fact_not_found), isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.toUiText(), isLoading = false) }
            }
        }
    }

    private fun toggleBookmark() {
        val fact = _state.value.fact ?: return
        viewModelScope.launch {
            try {
                toggleBookmarkUseCase(fact.id, fact.isBookmarked)
                eventTracker.trackBookmarkToggled(fact.id, !fact.isBookmarked)
                val updated = repository.getFactById(fact.id)
                _state.update { it.copy(fact = updated) }
            } catch (e: Exception) {
                _sideEffects.send(DetailSideEffect.ShowSnackbar(UiText.Resource(R.string.error_bookmark_failed)))
            }
        }
    }

    private fun updateNote(note: String) {
        savedStateHandle[DRAFT_NOTE_KEY] = note
        _state.update {
            it.copy(
                editedNote = note,
                hasNoteChanges = note != (_state.value.fact?.personalNote ?: "")
            )
        }
    }

    private fun saveNote() {
        val fact = _state.value.fact ?: return
        viewModelScope.launch {
            try {
                saveNoteUseCase(fact.id, _state.value.editedNote)
                savedStateHandle.remove<String>(DRAFT_NOTE_KEY)
                val updated = repository.getFactById(fact.id)
                _state.update { it.copy(fact = updated, hasNoteChanges = false) }
                _sideEffects.send(DetailSideEffect.ShowSnackbar(UiText.Resource(R.string.success_note_saved)))
            } catch (e: Exception) {
                _sideEffects.send(DetailSideEffect.ShowSnackbar(UiText.Resource(R.string.error_note_save_failed)))
            }
        }
    }

    private fun shareFact() {
        val fact = _state.value.fact ?: return
        viewModelScope.launch {
            _sideEffects.send(DetailSideEffect.ShareText(fact.fact))
        }
    }

    private fun Throwable.toUiText(): UiText = when (toErrorKind()) {
        is ErrorKind.Network -> UiText.Resource(R.string.error_network)
        is ErrorKind.Server -> UiText.Resource(R.string.error_server)
        is ErrorKind.Unknown -> UiText.Resource(R.string.error_load_detail_failed)
    }

    companion object {
        const val FACT_ID_KEY = "factId"
        const val DRAFT_NOTE_KEY = "draft_note"
    }
}
