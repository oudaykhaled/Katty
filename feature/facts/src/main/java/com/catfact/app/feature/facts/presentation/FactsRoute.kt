package com.catfact.app.feature.facts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catfact.app.core.designsystem.util.UiText
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is FactsSideEffect.NavigateToDetail -> currentOnNavigate(effect.factId)
                is FactsSideEffect.ShowSnackbar -> {
                    val message = when (val msg = effect.message) {
                        is UiText.Raw -> msg.value
                        is UiText.Resource -> context.getString(msg.resId, *msg.args.toTypedArray())
                    }
                    currentOnSnackbar(message)
                }
            }
        }
    }

    FactsScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
