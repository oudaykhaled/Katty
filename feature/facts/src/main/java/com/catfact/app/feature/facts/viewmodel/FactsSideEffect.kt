package com.catfact.app.feature.facts.viewmodel

import com.catfact.app.core.designsystem.util.UiText

sealed interface FactsSideEffect {
    data class NavigateToDetail(val factId: String) : FactsSideEffect
    data class ShowSnackbar(val message: UiText) : FactsSideEffect
}
