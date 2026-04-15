package com.catfact.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.catfact.app.core.designsystem.R
import com.catfact.app.core.designsystem.theme.sizing
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.domain.model.CatFact

@Composable
fun FactCard(
    fact: CatFact,
    onBookmarkClick: () -> Unit,
    onClick: (() -> Unit)? = null,
    isBookmarkLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val factContentDescription = stringResource(R.string.cd_cat_fact, fact.fact)
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .fillMaxWidth()
            .testTag("fact_card_${fact.id}")
            .semantics { contentDescription = factContentDescription },
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.sizing.elevationSmall)
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.extraLarge)) {
            Text(
                text = fact.fact,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                    CategoryChip(category = fact.category)
                    SyncIndicator(syncStatus = fact.syncStatus)
                }
                BookmarkButton(
                    isBookmarked = fact.isBookmarked,
                    onClick = onBookmarkClick,
                    isLoading = isBookmarkLoading
                )
            }
        }
    }
}
