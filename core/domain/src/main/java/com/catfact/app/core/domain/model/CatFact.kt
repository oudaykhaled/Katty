package com.catfact.app.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class CatFact(
    val id: String,
    val fact: String,
    val length: Int,
    val isBookmarked: Boolean = false,
    val personalNote: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val category: FactCategory = FactCategory.fromLength(length)
) {
    fun withBookmark(bookmarked: Boolean): CatFact = copy(isBookmarked = bookmarked)
    fun withNote(note: String): CatFact = copy(personalNote = note)
    fun withSyncStatus(status: SyncStatus): CatFact = copy(syncStatus = status)
}
