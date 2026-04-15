package com.catfact.app.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.R
import com.catfact.app.core.designsystem.theme.sizing
import com.catfact.app.core.domain.model.SyncStatus

@Composable
fun SyncIndicator(
    syncStatus: SyncStatus,
    modifier: Modifier = Modifier
) {
    val iconSize = MaterialTheme.sizing.iconSmall
    when (syncStatus) {
        SyncStatus.SYNCED -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.sync_status_synced),
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.size(iconSize)
            )
        }
        SyncStatus.PENDING -> {
            val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "sync_rotation_value"
            )
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = stringResource(R.string.sync_status_syncing),
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .size(iconSize)
                    .graphicsLayer { rotationZ = rotation }
            )
        }
        SyncStatus.FAILED -> {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = stringResource(R.string.sync_status_failed),
                tint = MaterialTheme.colorScheme.error,
                modifier = modifier.size(iconSize)
            )
        }
    }
}
