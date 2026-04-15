package com.catfact.app.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.catfact.app.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottiePullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            LottiePullIndicator(
                isRefreshing = isRefreshing,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LottiePullIndicator(
    isRefreshing: Boolean,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_cat))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
    )

    val dragProgress = state.distanceFraction.coerceIn(0f, 1f)

    val alpha by animateFloatAsState(
        targetValue = if (isRefreshing) 1f else dragProgress,
        label = "lottie_alpha",
    )

    if (isRefreshing || dragProgress > 0f) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            LottieAnimation(
                composition = composition,
                progress = { if (isRefreshing) progress else dragProgress },
                modifier = Modifier
                    .size(64.dp)
                    .alpha(alpha),
            )
        }
    }
}
