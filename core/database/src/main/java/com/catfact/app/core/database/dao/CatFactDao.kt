package com.catfact.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.catfact.app.core.database.entity.CatFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CatFactDao {

    @Query("SELECT * FROM cat_facts ORDER BY cachedAt DESC")
    abstract fun observeAllFacts(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE isBookmarked = 1 ORDER BY cachedAt DESC")
    abstract fun observeFavorites(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE id = :id")
    abstract suspend fun getFactById(id: String): CatFactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFacts(facts: List<CatFactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFact(fact: CatFactEntity)

    @Query("UPDATE cat_facts SET isBookmarked = :isBookmarked, syncStatus = :syncStatus WHERE id = :factId")
    abstract suspend fun updateBookmark(factId: String, isBookmarked: Boolean, syncStatus: String)

    @Query("UPDATE cat_facts SET personalNote = :note, syncStatus = :syncStatus WHERE id = :factId")
    abstract suspend fun updateNote(factId: String, note: String, syncStatus: String)

    @Query("SELECT id FROM cat_facts WHERE syncStatus IN ('PENDING', 'FAILED')")
    abstract suspend fun getPendingFactIds(): List<String>

    @Query("DELETE FROM cat_facts WHERE syncStatus = 'SYNCED'")
    abstract suspend fun deleteSyncedFacts()

    @Query("SELECT * FROM cat_facts WHERE fact LIKE '%' || :query || '%'")
    abstract fun searchFacts(query: String): Flow<List<CatFactEntity>>

    @Transaction
    open suspend fun replaceAllFacts(facts: List<CatFactEntity>) {
        val pendingIds = getPendingFactIds()
        deleteSyncedFacts()
        val safeToInsert = facts.filter { it.id !in pendingIds }
        insertFacts(safeToInsert)
    }
}
