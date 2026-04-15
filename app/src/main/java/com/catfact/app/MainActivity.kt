package com.catfact.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val deepLinkFactId = parseDeepLinkUri(intent?.data)
        setContent {
            CatFactTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .semantics { testTagsAsResourceId = true }
                ) {
                    AppNavigation(initialDeepLinkFactId = deepLinkFactId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
