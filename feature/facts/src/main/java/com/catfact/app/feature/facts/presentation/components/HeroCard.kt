package com.catfact.app.feature.facts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.catfact.app.core.designsystem.theme.sizing
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.facts.R

@Composable
fun HeroCard(
    fact: CatFact,
    onNewFactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.sizing.elevationMedium)
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.xxLarge)) {
            Text(
                text = stringResource(R.string.hero_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = fact.fact,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(MaterialTheme.spacing.large))
            Button(
                onClick = onNewFactClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("new_fact_button")
            ) {
                Text(stringResource(R.string.hero_new_fact))
            }
        }
    }
}
