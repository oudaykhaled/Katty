package com.catfact.app.feature.factdetail.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catfact.app.core.designsystem.util.UiText
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

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is DetailSideEffect.NavigateBack -> currentOnBack()
                is DetailSideEffect.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    val chooserTitle = context.getString(R.string.detail_share_chooser_title)
                    context.startActivity(Intent.createChooser(intent, chooserTitle))
                }
                is DetailSideEffect.ShowSnackbar -> {
                    val message = when (val msg = effect.message) {
                        is UiText.Raw -> msg.value
                        is UiText.Resource -> context.getString(msg.resId, *msg.args.toTypedArray())
                    }
                    currentOnSnackbar(message)
                }
            }
        }
    }

    DetailScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
