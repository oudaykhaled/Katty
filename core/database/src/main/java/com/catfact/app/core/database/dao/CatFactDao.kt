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

    @Query("SELECT * FROM cat_facts WHERE feedPosition IS NOT NULL ORDER BY feedPosition ASC, id ASC")
    abstract fun observeAllFacts(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE isBookmarked = 1 ORDER BY cachedAt DESC, id ASC")
    abstract fun observeFavorites(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE id = :id")
    abstract suspend fun getFactById(id: String): CatFactEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertMissingFacts(facts: List<CatFactEntity>)

    @Query(
        """
        UPDATE cat_facts SET fact = :fact, length = :length, cachedAt = :cachedAt
        WHERE id = :id AND syncStatus = 'SYNCED'
        """
    )
    protected abstract suspend fun updateRemoteFields(id: String, fact: String, length: Int, cachedAt: Long)

    @Query(
        """
        UPDATE cat_facts SET feedPosition = CASE
            WHEN feedPosition IS NULL OR :position < feedPosition THEN :position
            ELSE feedPosition END
        WHERE id = :id
        """
    )
    protected abstract suspend fun updateFeedPosition(id: String, position: Long)

    // Personal annotations belong to this device. A network response never replaces them.
    @Transaction
    open suspend fun insertFacts(facts: List<CatFactEntity>): List<CatFactEntity> {
        insertMissingFacts(facts)
        return facts.map { incoming ->
            updateRemoteFields(incoming.id, incoming.fact, incoming.length, incoming.cachedAt)
            incoming.feedPosition?.let { updateFeedPosition(incoming.id, it) }
            checkNotNull(getFactById(incoming.id))
        }
    }

    @Transaction
    open suspend fun insertFact(fact: CatFactEntity): CatFactEntity = insertFacts(listOf(fact)).single()

    @Query("UPDATE cat_facts SET isBookmarked = :isBookmarked WHERE id = :factId")
    abstract suspend fun updateBookmark(factId: String, isBookmarked: Boolean): Int

    @Query("UPDATE cat_facts SET personalNote = :note WHERE id = :factId")
    abstract suspend fun updateNote(factId: String, note: String): Int

    @Query("SELECT id FROM cat_facts WHERE syncStatus IN ('PENDING', 'FAILED')")
    abstract suspend fun getPendingFactIds(): List<String>

    @Query("UPDATE cat_facts SET feedPosition = NULL")
    protected abstract suspend fun clearFeedPositions()

    @Query(
        """
        SELECT * FROM cat_facts
        WHERE feedPosition IS NOT NULL AND fact LIKE '%' || :query || '%'
        ORDER BY feedPosition ASC, id ASC
        """
    )
    abstract fun searchFacts(query: String): Flow<List<CatFactEntity>>

    @Transaction
    open suspend fun replaceAllFacts(facts: List<CatFactEntity>): List<CatFactEntity> {
        // Feed membership is separate from lifetime: an open detail remains editable.
        clearFeedPositions()
        return insertFacts(facts)
    }
}
