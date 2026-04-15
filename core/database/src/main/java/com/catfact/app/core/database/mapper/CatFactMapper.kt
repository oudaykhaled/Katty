package com.catfact.app.core.database.mapper

import com.catfact.app.core.database.entity.CatFactEntity
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.FactCategory
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.network.model.CatFactResponse
import java.util.UUID

fun CatFactEntity.toDomain(): CatFact = CatFact(
    id = id,
    fact = fact,
    length = length,
    isBookmarked = isBookmarked,
    personalNote = personalNote,
    syncStatus = SyncStatus.entries.find { it.name == syncStatus } ?: SyncStatus.SYNCED,
    category = FactCategory.fromLength(length)
)

fun CatFact.toEntity(): CatFactEntity = CatFactEntity(
    id = id,
    fact = fact,
    length = length,
    isBookmarked = isBookmarked,
    personalNote = personalNote,
    syncStatus = syncStatus.name
)

fun CatFactResponse.toEntity(id: String = UUID.nameUUIDFromBytes(fact.toByteArray()).toString()): CatFactEntity =
    CatFactEntity(
        id = id,
        fact = fact,
        length = length
    )
