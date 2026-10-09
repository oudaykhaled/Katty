package com.catfact.app.feature.factdetail.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catfact.app.core.designsystem.util.resolve
import com.catfact.app.feature.factdetail.R
import com.catfact.app.feature.factdetail.viewmodel.DetailSideEffect
import com.catfact.app.feature.factdetail.viewmodel.DetailViewModel

@Composable
fun DetailRoute(
    factId: String,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(factId) }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnBack by rememberUpdatedState(onNavigateBack)
    val currentOnSnackbar by rememberUpdatedState(onShowSnackbar)
    val context = LocalContext.current
    val resources = LocalResources.current
    val currentResources by rememberUpdatedState(resources)
    val chooserTitle = stringResource(R.string.detail_share_chooser_title)
    val currentChooserTitle by rememberUpdatedState(chooserTitle)

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is DetailSideEffect.NavigateBack -> currentOnBack()
                is DetailSideEffect.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    context.startActivity(Intent.createChooser(intent, currentChooserTitle))
                }
                is DetailSideEffect.ShowSnackbar -> {
                    currentOnSnackbar(effect.message.resolve(currentResources))
                }
            }
        }
    }

    DetailScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
