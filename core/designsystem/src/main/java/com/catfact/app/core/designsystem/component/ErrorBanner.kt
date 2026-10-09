package com.catfact.app.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.catfact.app.core.designsystem.theme.spacing
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.designsystem.util.asString

@Composable
fun ErrorBanner(error: UiText, dismissDescription: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium).testTag("error_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = error.asString(), color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_error")) {
            Icon(Icons.Filled.Close, contentDescription = dismissDescription, tint = MaterialTheme.colorScheme.error)
        }
    }
}
