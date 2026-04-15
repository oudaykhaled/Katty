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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.catfact.app.core.designsystem.component.ConnectivityBanner
import com.catfact.app.core.designsystem.component.FactCard
import com.catfact.app.core.designsystem.component.LottiePullToRefreshBox
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.designsystem.util.asString
import com.catfact.app.feature.facts.R
import com.catfact.app.feature.facts.presentation.components.HeroCard
import com.catfact.app.feature.facts.presentation.components.SearchBar
import com.catfact.app.feature.facts.viewmodel.FactsEvent
import com.catfact.app.feature.facts.viewmodel.FactsUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactsScreen(
    state: FactsUiState,
    onEvent: (FactsEvent) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible to totalItems
        }
            .distinctUntilChanged()
            .filter { (lastVisible, total) -> total > 0 && lastVisible >= total - 3 }
            .collect { onEvent(FactsEvent.LoadNextPage) }
    }

    val filteredFacts = remember(state.facts, state.searchQuery) {
        if (state.searchQuery.isBlank()) state.facts
        else state.facts.filter { it.fact.contains(state.searchQuery, ignoreCase = true) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.facts_title)) })
        },
        floatingActionButton = {
            AnimatedVisibility(visible = showScrollToTop) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier.testTag("scroll_to_top_fab")
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = stringResource(R.string.cd_scroll_to_top))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ConnectivityBanner()

            state.error?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.medium)
                        .testTag("error_banner"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error.asString(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { onEvent(FactsEvent.DismissError) }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_dismiss_error),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            LottiePullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { onEvent(FactsEvent.Refresh) },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pull_to_refresh")
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(MaterialTheme.spacing.extraLarge),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("facts_list")
                ) {
                    state.randomFact?.let { heroFact ->
                        item(key = "hero", contentType = "hero") {
                            HeroCard(
                                fact = heroFact,
                                onNewFactClick = { onEvent(FactsEvent.FetchRandomFact) },
                                modifier = Modifier
                                    .padding(bottom = MaterialTheme.spacing.extraLarge)
                                    .testTag("hero_card")
                            )
                        }
                    }

                    item(key = "search", contentType = "search") {
                        SearchBar(
                            query = state.searchInput,
                            onQueryChange = { onEvent(FactsEvent.Search(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = MaterialTheme.spacing.extraLarge)
                        )
                    }

                    items(
                        items = filteredFacts,
                        key = { it.id },
                        contentType = { "fact" }
                    ) { fact ->
                        FactCard(
                            fact = fact,
                            onBookmarkClick = {
                                onEvent(FactsEvent.ToggleBookmark(fact.id, fact.isBookmarked))
                            },
                            onClick = { onEvent(FactsEvent.FactClicked(fact.id)) },
                            isBookmarkLoading = fact.id in state.bookmarkLoadingIds,
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                        )
                    }

                    if (state.isLoadingMore) {
                        item(key = "loading_more", contentType = "loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.spacing.extraLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
