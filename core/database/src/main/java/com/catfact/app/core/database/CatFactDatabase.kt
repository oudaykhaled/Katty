package com.catfact.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.entity.CatFactEntity

@Database(
    entities = [CatFactEntity::class],
    version = 2,
    exportSchema = true
)
abstract class CatFactDatabase : RoomDatabase() {
    abstract fun catFactDao(): CatFactDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cat_facts ADD COLUMN feedPosition INTEGER")
                // Retain the legacy offline feed in its previous order without changing freshness.
                // Correlated counts also work on SQLite versions predating window functions.
                db.execSQL(
                    """
                    UPDATE cat_facts SET feedPosition = (
                        SELECT COUNT(*) FROM cat_facts AS preceding
                        WHERE preceding.cachedAt > cat_facts.cachedAt
                           OR (preceding.cachedAt = cat_facts.cachedAt AND preceding.id < cat_facts.id)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
