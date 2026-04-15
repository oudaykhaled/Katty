package com.catfact.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

class AppRoute {
    @Serializable
    data object FactsRoute : NavKey

    @Serializable
    data object FavoritesRoute : NavKey

    @Serializable
    data class FactDetailRoute(val factId: String) : NavKey
}
