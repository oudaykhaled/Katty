package com.catfact.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.catfact.app.core.designsystem.theme.CatFactTheme
import com.catfact.app.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

fun parseDeepLinkUri(uri: Uri?): String? {
    if (uri == null) return null
    if (uri.scheme != "catfact" || uri.host != "fact") return null
    return uri.pathSegments.firstOrNull()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingDeepLinkFactId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingDeepLinkFactId = if (savedInstanceState == null) {
            parseDeepLinkUri(intent?.data)
        } else {
            savedInstanceState.getString(PENDING_DEEP_LINK_KEY)
        }
        setContent {
            CatFactTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .semantics { testTagsAsResourceId = true }
                ) {
                    AppNavigation(
                        initialDeepLinkFactId = pendingDeepLinkFactId,
                        onDeepLinkConsumed = { pendingDeepLinkFactId = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDeepLinkFactId = parseDeepLinkUri(intent.data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        pendingDeepLinkFactId?.let { outState.putString(PENDING_DEEP_LINK_KEY, it) }
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val PENDING_DEEP_LINK_KEY = "pending_deep_link_fact_id"
    }
}
