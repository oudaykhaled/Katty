package com.catfact.app.feature.favorites.viewmodel

import androidx.compose.runtime.Immutable
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FavoritesUiState(
    val favorites: ImmutableList<CatFact> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
