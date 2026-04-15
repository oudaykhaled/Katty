package com.catfact.app.feature.facts.viewmodel

sealed interface FactsEvent {
    data object Refresh : FactsEvent
    data object LoadNextPage : FactsEvent
    data class ToggleBookmark(val factId: String, val isCurrentlyBookmarked: Boolean) : FactsEvent
    data class Search(val query: String) : FactsEvent
    data object DismissError : FactsEvent
    data class FactClicked(val factId: String) : FactsEvent
    data object FetchRandomFact : FactsEvent
}
