package com.catfact.app.feature.factdetail.viewmodel

import com.catfact.app.core.designsystem.util.UiText

sealed interface DetailSideEffect {
    data object NavigateBack : DetailSideEffect
    data class ShareText(val text: String) : DetailSideEffect
    data class ShowSnackbar(val message: UiText) : DetailSideEffect
}
