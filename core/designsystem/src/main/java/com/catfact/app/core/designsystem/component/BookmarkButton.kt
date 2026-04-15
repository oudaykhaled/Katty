package com.catfact.app.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.catfact.app.core.designsystem.R
import com.catfact.app.core.designsystem.theme.sizing

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var hasBeenComposed by remember { mutableStateOf(false) }
    val animSpec = if (hasBeenComposed) {
        spring<Float>(stiffness = Spring.StiffnessMediumLow)
    } else {
        snap()
    }

    val scale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.2f else 1f,
        animationSpec = animSpec,
        label = "bookmark_scale",
        finishedListener = { hasBeenComposed = true }
    )
    val tint by animateColorAsState(
        targetValue = if (isBookmarked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "bookmark_tint"
    )

    val description = if (isBookmarked) {
        stringResource(R.string.bookmark_remove)
    } else {
        stringResource(R.string.bookmark_add)
    }

    if (isLoading) {
        CircularProgressIndicator(
            modifier = modifier.size(MaterialTheme.sizing.iconMedium),
            strokeWidth = MaterialTheme.sizing.strokeThin
        )
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier.semantics { contentDescription = description }
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            )
        }
    }
}
