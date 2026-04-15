package com.catfact.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.catfact.app.R
import com.catfact.app.feature.factdetail.presentation.DetailRoute
import com.catfact.app.feature.facts.presentation.FactsRoute
import com.catfact.app.feature.favorites.presentation.FavoritesRoute
import kotlinx.coroutines.launch

private enum class BottomTab(
    val route: NavKey,
    @StringRes val labelRes: Int,
    val testTag: String
) {
    FACTS(AppRoute.FactsRoute, R.string.nav_label_facts, "nav_facts"),
    FAVORITES(AppRoute.FavoritesRoute, R.string.nav_label_favorites, "nav_favorites")
}

@Composable
fun AppNavigation(
    initialDeepLinkFactId: String? = null
) {
    val backStack = rememberNavBackStack(AppRoute.FactsRoute)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(initialDeepLinkFactId) {
        if (initialDeepLinkFactId != null) {
            backStack.add(AppRoute.FactDetailRoute(initialDeepLinkFactId))
        }
    }

    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(hostState = snackbarHostState)

        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            modifier = Modifier.weight(1f),
            entryProvider = entryProvider {
                entry<AppRoute.FactsRoute> {
                    FactsRoute(
                        onNavigateToDetail = { factId ->
                            backStack.add(AppRoute.FactDetailRoute(factId))
                        },
                        onShowSnackbar = showSnackbar
                    )
                }
                entry<AppRoute.FavoritesRoute> {
                    FavoritesRoute(
                        onNavigateToDetail = { factId ->
                            backStack.add(AppRoute.FactDetailRoute(factId))
                        },
                        onShowSnackbar = showSnackbar
                    )
                }
                entry<AppRoute.FactDetailRoute> { route ->
                    DetailRoute(
                        factId = route.factId,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },
                        onShowSnackbar = showSnackbar
                    )
                }
            }
        )

        NavigationBar(modifier = Modifier.testTag("bottom_navigation_bar")) {
            BottomTab.entries.forEachIndexed { index, tab ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = {
                        if (selectedTab != index) {
                            selectedTab = index
                            backStack.clear()
                            backStack.add(tab.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = when (tab) {
                                BottomTab.FACTS ->
                                    if (selectedTab == index) Icons.AutoMirrored.Filled.List
                                    else Icons.AutoMirrored.Outlined.List
                                BottomTab.FAVORITES ->
                                    if (selectedTab == index) Icons.Filled.Favorite
                                    else Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = stringResource(tab.labelRes)
                        )
                    },
                    label = { Text(stringResource(tab.labelRes)) },
                    modifier = Modifier.testTag(tab.testTag)
                )
            }
        }
    }
}
