package com.catfact.app.feature.facts.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.component.ConnectivityBanner
import com.catfact.app.core.designsystem.component.ErrorBanner
import com.catfact.app.core.designsystem.component.FactCard
import com.catfact.app.core.designsystem.component.LottiePullToRefreshBox
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.facts.R
import com.catfact.app.feature.facts.presentation.components.HeroCard
import com.catfact.app.feature.facts.presentation.components.SearchBar
import com.catfact.app.feature.facts.viewmodel.FactsEvent
import com.catfact.app.feature.facts.viewmodel.FactsUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val ScrollToTopItemIndex = 3
private const val PagePrefetchDistance = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactsScreen(state: FactsUiState, onEvent: (FactsEvent) -> Unit) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > ScrollToTopItemIndex }
    }
    ObservePagination(state, listState, onEvent)
    val filteredFacts = remember(state.facts, state.searchQuery) {
        if (state.searchQuery.isBlank()) state.facts
        else state.facts.filter { it.fact.contains(state.searchQuery, ignoreCase = true) }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.facts_title)) }) },
        floatingActionButton = {
            AnimatedVisibility(visible = showScrollToTop) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier.testTag("scroll_to_top_fab")
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, stringResource(R.string.cd_scroll_to_top))
                }
            }
        }
    ) { padding ->
        FactsContent(state, filteredFacts, listState, onEvent, Modifier.padding(padding))
    }
}

@Composable
private fun ObservePagination(state: FactsUiState, listState: LazyListState, onEvent: (FactsEvent) -> Unit) {
    var lastTrigger by remember { mutableStateOf<Pair<Int, String>?>(null) }
    LaunchedEffect(
        listState, state.isLoading, state.isLoadingMore, state.hasMorePages,
        state.searchQuery, state.currentPage, state.facts.lastOrNull()?.id
    ) {
        if (state.isLoading) lastTrigger = null
        val canPaginate = !state.isLoading && !state.isLoadingMore && state.hasMorePages
        if (!canPaginate || state.searchQuery.isNotBlank()) return@LaunchedEffect
        // Loading footers never participate in the request latch or near-end detection.
        val prefetchIds = state.facts.takeLast(PagePrefetchDistance).map { it.id }.toSet()
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.key in prefetchIds } }
            .distinctUntilChanged()
            .collect { nearEnd ->
                val lastId = state.facts.lastOrNull()?.id ?: return@collect
                val trigger = state.currentPage to lastId
                if (!nearEnd) lastTrigger = null
                else if (lastTrigger != trigger) {
                    lastTrigger = trigger
                    onEvent(FactsEvent.LoadNextPage)
                }
            }
    }
}

@Composable
private fun FactsContent(
    state: FactsUiState,
    filteredFacts: List<CatFact>,
    listState: LazyListState,
    onEvent: (FactsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ConnectivityBanner()
        state.error?.let { error ->
            ErrorBanner(error, stringResource(R.string.cd_dismiss_error)) { onEvent(FactsEvent.DismissError) }
        }
        LottiePullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(FactsEvent.Refresh) },
            modifier = Modifier.fillMaxSize().testTag("pull_to_refresh")
        ) {
            FactsList(state, filteredFacts, listState, onEvent)
        }
    }
}

@Composable
private fun FactsList(
    state: FactsUiState,
    facts: List<CatFact>,
    listState: LazyListState,
    onEvent: (FactsEvent) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(MaterialTheme.spacing.extraLarge),
        modifier = Modifier.fillMaxSize().testTag("facts_list")
    ) {
        factHeaders(state, onEvent)
        items(items = facts, key = { it.id }, contentType = { "fact" }) { fact ->
            FactCard(
                fact = fact,
                onBookmarkClick = { onEvent(FactsEvent.ToggleBookmark(fact.id, fact.isBookmarked)) },
                onClick = { onEvent(FactsEvent.FactClicked(fact.id)) },
                isBookmarkLoading = fact.id in state.bookmarkLoadingIds,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
            )
        }
        if (state.isLoadingMore) {
            item(key = "loading_more", contentType = "loading") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.extraLarge),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }
}

private fun LazyListScope.factHeaders(state: FactsUiState, onEvent: (FactsEvent) -> Unit) {
    state.randomFact?.let { heroFact ->
        item(key = "hero", contentType = "hero") {
            HeroCard(
                fact = heroFact,
                onNewFactClick = { onEvent(FactsEvent.FetchRandomFact) },
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.extraLarge).testTag("hero_card")
            )
        }
    }
    item(key = "search", contentType = "search") {
        SearchBar(
            query = state.searchInput,
            onQueryChange = { onEvent(FactsEvent.Search(it)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.extraLarge)
        )
    }
}
