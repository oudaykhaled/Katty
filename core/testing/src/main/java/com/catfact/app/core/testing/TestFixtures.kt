package com.catfact.app.core.testing

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.FactCategory
import com.catfact.app.core.domain.model.SyncStatus

object TestFixtures {

    fun createCatFact(
        id: String = "fact-1",
        fact: String = "Cats sleep 70% of their lives.",
        length: Int = 31,
        isBookmarked: Boolean = false,
        personalNote: String = "",
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        category: FactCategory = FactCategory.fromLength(length)
    ) = CatFact(
        id = id,
        fact = fact,
        length = length,
        isBookmarked = isBookmarked,
        personalNote = personalNote,
        syncStatus = syncStatus,
        category = category
    )

    fun createFactList(count: Int = 5): List<CatFact> =
        (1..count).map { i ->
            createCatFact(
                id = "fact-$i",
                fact = "Cat fact number $i with some interesting content about cats.",
                length = 55 + i
            )
        }
}
