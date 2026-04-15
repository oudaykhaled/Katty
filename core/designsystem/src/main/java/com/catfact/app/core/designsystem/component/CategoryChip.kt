package com.catfact.app.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.R
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.domain.model.FactCategory

@Composable
fun CategoryChip(
    category: FactCategory,
    modifier: Modifier = Modifier
) {
    val color = when (category) {
        FactCategory.Short -> MaterialTheme.colorScheme.tertiary
        FactCategory.Medium -> MaterialTheme.colorScheme.secondary
        FactCategory.Long -> MaterialTheme.colorScheme.primary
    }

    val displayName = when (category) {
        FactCategory.Short -> stringResource(R.string.category_short)
        FactCategory.Medium -> stringResource(R.string.category_medium)
        FactCategory.Long -> stringResource(R.string.category_long)
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small
            )
        )
    }
}
