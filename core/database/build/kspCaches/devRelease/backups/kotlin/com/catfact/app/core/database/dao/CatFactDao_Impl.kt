package com.catfact.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.catfact.app.core.database.entity.CatFactEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CatFactDao_Impl(
  __db: RoomDatabase,
) : CatFactDao() {
  private val __db: RoomDatabase

  private val __insertAdapterOfCatFactEntity: EntityInsertAdapter<CatFactEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCatFactEntity = object : EntityInsertAdapter<CatFactEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cat_facts` (`id`,`fact`,`length`,`isBookmarked`,`personalNote`,`syncStatus`,`cachedAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CatFactEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.fact)
        statement.bindLong(3, entity.length.toLong())
        val _tmp: Int = if (entity.isBookmarked) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.personalNote)
        statement.bindText(6, entity.syncStatus)
        statement.bindLong(7, entity.cachedAt)
      }
    }
  }

  public override suspend fun insertFacts(facts: List<CatFactEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCatFactEntity.insert(_connection, facts)
  }

  public override suspend fun insertFact(fact: CatFactEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCatFactEntity.insert(_connection, fact)
  }

  public override suspend fun replaceAllFacts(facts: List<CatFactEntity>): Unit =
      performInTransactionSuspending(__db) {
    super@CatFactDao_Impl.replaceAllFacts(facts)
  }

  public override fun observeAllFacts(): Flow<List<CatFactEntity>> {
    val _sql: String = "SELECT * FROM cat_facts ORDER BY cachedAt DESC"
    return createFlow(__db, false, arrayOf("cat_facts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFact: Int = getColumnIndexOrThrow(_stmt, "fact")
        val _columnIndexOfLength: Int = getColumnIndexOrThrow(_stmt, "length")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfPersonalNote: Int = getColumnIndexOrThrow(_stmt, "personalNote")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<CatFactEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CatFactEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFact: String
          _tmpFact = _stmt.getText(_columnIndexOfFact)
          val _tmpLength: Int
          _tmpLength = _stmt.getLong(_columnIndexOfLength).toInt()
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpPersonalNote: String
          _tmpPersonalNote = _stmt.getText(_columnIndexOfPersonalNote)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              CatFactEntity(_tmpId,_tmpFact,_tmpLength,_tmpIsBookmarked,_tmpPersonalNote,_tmpSyncStatus,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeFavorites(): Flow<List<CatFactEntity>> {
    val _sql: String = "SELECT * FROM cat_facts WHERE isBookmarked = 1 ORDER BY cachedAt DESC"
    return createFlow(__db, false, arrayOf("cat_facts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFact: Int = getColumnIndexOrThrow(_stmt, "fact")
        val _columnIndexOfLength: Int = getColumnIndexOrThrow(_stmt, "length")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfPersonalNote: Int = getColumnIndexOrThrow(_stmt, "personalNote")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<CatFactEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CatFactEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFact: String
          _tmpFact = _stmt.getText(_columnIndexOfFact)
          val _tmpLength: Int
          _tmpLength = _stmt.getLong(_columnIndexOfLength).toInt()
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpPersonalNote: String
          _tmpPersonalNote = _stmt.getText(_columnIndexOfPersonalNote)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              CatFactEntity(_tmpId,_tmpFact,_tmpLength,_tmpIsBookmarked,_tmpPersonalNote,_tmpSyncStatus,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFactById(id: String): CatFactEntity? {
    val _sql: String = "SELECT * FROM cat_facts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFact: Int = getColumnIndexOrThrow(_stmt, "fact")
        val _columnIndexOfLength: Int = getColumnIndexOrThrow(_stmt, "length")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfPersonalNote: Int = getColumnIndexOrThrow(_stmt, "personalNote")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: CatFactEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFact: String
          _tmpFact = _stmt.getText(_columnIndexOfFact)
          val _tmpLength: Int
          _tmpLength = _stmt.getLong(_columnIndexOfLength).toInt()
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpPersonalNote: String
          _tmpPersonalNote = _stmt.getText(_columnIndexOfPersonalNote)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _result =
              CatFactEntity(_tmpId,_tmpFact,_tmpLength,_tmpIsBookmarked,_tmpPersonalNote,_tmpSyncStatus,_tmpCachedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPendingFactIds(): List<String> {
    val _sql: String = "SELECT id FROM cat_facts WHERE syncStatus IN ('PENDING', 'FAILED')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchFacts(query: String): Flow<List<CatFactEntity>> {
    val _sql: String = "SELECT * FROM cat_facts WHERE fact LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("cat_facts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFact: Int = getColumnIndexOrThrow(_stmt, "fact")
        val _columnIndexOfLength: Int = getColumnIndexOrThrow(_stmt, "length")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfPersonalNote: Int = getColumnIndexOrThrow(_stmt, "personalNote")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<CatFactEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CatFactEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFact: String
          _tmpFact = _stmt.getText(_columnIndexOfFact)
          val _tmpLength: Int
          _tmpLength = _stmt.getLong(_columnIndexOfLength).toInt()
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpPersonalNote: String
          _tmpPersonalNote = _stmt.getText(_columnIndexOfPersonalNote)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              CatFactEntity(_tmpId,_tmpFact,_tmpLength,_tmpIsBookmarked,_tmpPersonalNote,_tmpSyncStatus,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBookmark(
    factId: String,
    isBookmarked: Boolean,
    syncStatus: String,
  ) {
    val _sql: String = "UPDATE cat_facts SET isBookmarked = ?, syncStatus = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isBookmarked) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, syncStatus)
        _argIndex = 3
        _stmt.bindText(_argIndex, factId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateNote(
    factId: String,
    note: String,
    syncStatus: String,
  ) {
    val _sql: String = "UPDATE cat_facts SET personalNote = ?, syncStatus = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, note)
        _argIndex = 2
        _stmt.bindText(_argIndex, syncStatus)
        _argIndex = 3
        _stmt.bindText(_argIndex, factId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSyncedFacts() {
    val _sql: String = "DELETE FROM cat_facts WHERE syncStatus = 'SYNCED'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
