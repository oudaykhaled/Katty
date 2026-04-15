package com.catfact.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_facts")
data class CatFactEntity(
    @PrimaryKey val id: String,
    val fact: String,
    val length: Int,
    val isBookmarked: Boolean = false,
    val personalNote: String = "",
    val syncStatus: String = "SYNCED",
    val cachedAt: Long = System.currentTimeMillis()
)
