package com.catfact.app.feature.facts.viewmodel

import androidx.compose.runtime.Immutable
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FactsUiState(
    val facts: ImmutableList<CatFact> = persistentListOf(),
    val randomFact: CatFact? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: UiText? = null,
    val searchInput: String = "",
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val bookmarkLoadingIds: ImmutableList<String> = persistentListOf()
)
