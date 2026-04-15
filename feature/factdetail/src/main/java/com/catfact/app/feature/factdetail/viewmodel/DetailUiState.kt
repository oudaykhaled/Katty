package com.catfact.app.feature.factdetail.viewmodel

import androidx.compose.runtime.Immutable
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact

@Immutable
data class DetailUiState(
    val fact: CatFact? = null,
    val editedNote: String = "",
    val hasNoteChanges: Boolean = false,
    val isLoading: Boolean = true,
    val error: UiText? = null
)
