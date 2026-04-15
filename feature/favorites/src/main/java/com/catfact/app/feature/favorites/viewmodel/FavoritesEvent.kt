package com.catfact.app.feature.favorites.viewmodel

sealed interface FavoritesEvent {
    data class RemoveBookmark(val factId: String) : FavoritesEvent
    data object DismissError : FavoritesEvent
    data class FactClicked(val factId: String) : FavoritesEvent
}
