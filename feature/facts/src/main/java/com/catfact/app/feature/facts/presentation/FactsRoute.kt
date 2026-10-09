package com.catfact.app.feature.facts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catfact.app.core.designsystem.util.resolve
import com.catfact.app.feature.facts.viewmodel.FactsSideEffect
import com.catfact.app.feature.facts.viewmodel.FactsViewModel

@Composable
fun FactsRoute(
    onNavigateToDetail: (String) -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: FactsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnNavigate by rememberUpdatedState(onNavigateToDetail)
    val currentOnSnackbar by rememberUpdatedState(onShowSnackbar)
    val resources = LocalResources.current
    val currentResources by rememberUpdatedState(resources)

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is FactsSideEffect.NavigateToDetail -> currentOnNavigate(effect.factId)
                is FactsSideEffect.ShowSnackbar -> {
                    currentOnSnackbar(effect.message.resolve(currentResources))
                }
            }
        }
    }

    FactsScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
