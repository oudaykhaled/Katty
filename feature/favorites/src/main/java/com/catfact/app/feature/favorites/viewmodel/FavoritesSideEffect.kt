package com.catfact.app.feature.favorites.viewmodel

import com.catfact.app.core.designsystem.util.UiText

sealed interface FavoritesSideEffect {
    data class NavigateToDetail(val factId: String) : FavoritesSideEffect
    data class ShowSnackbar(val message: UiText) : FavoritesSideEffect
}
