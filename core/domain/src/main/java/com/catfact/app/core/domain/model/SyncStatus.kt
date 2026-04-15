package com.catfact.app.core.domain.model

import androidx.compose.runtime.Stable

@Stable
enum class SyncStatus {
    SYNCED,
    PENDING,
    FAILED
}
