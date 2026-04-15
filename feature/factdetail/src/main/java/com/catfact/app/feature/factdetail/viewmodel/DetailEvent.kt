package com.catfact.app.feature.factdetail.viewmodel

sealed interface DetailEvent {
    data object ToggleBookmark : DetailEvent
    data class UpdateNote(val note: String) : DetailEvent
    data object SaveNote : DetailEvent
    data object ShareFact : DetailEvent
    data object NavigateBack : DetailEvent
}
