package com.catfact.app.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.dao.CatFactDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CatFactDatabase_Impl : CatFactDatabase() {
  private val _catFactDao: Lazy<CatFactDao> = lazy {
    CatFactDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "aed7efdb5a021340df43feea162d85be", "1250a9e09119abd644a185d07144f564") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cat_facts` (`id` TEXT NOT NULL, `fact` TEXT NOT NULL, `length` INTEGER NOT NULL, `isBookmarked` INTEGER NOT NULL, `personalNote` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'aed7efdb5a021340df43feea162d85be')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `cat_facts`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCatFacts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCatFacts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("fact", TableInfo.Column("fact", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("length", TableInfo.Column("length", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("isBookmarked", TableInfo.Column("isBookmarked", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("personalNote", TableInfo.Column("personalNote", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("syncStatus", TableInfo.Column("syncStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCatFacts.put("cachedAt", TableInfo.Column("cachedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCatFacts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCatFacts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCatFacts: TableInfo = TableInfo("cat_facts", _columnsCatFacts,
            _foreignKeysCatFacts, _indicesCatFacts)
        val _existingCatFacts: TableInfo = read(connection, "cat_facts")
        if (!_infoCatFacts.equals(_existingCatFacts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cat_facts(com.catfact.app.core.database.entity.CatFactEntity).
              | Expected:
              |""".trimMargin() + _infoCatFacts + """
              |
              | Found:
              |""".trimMargin() + _existingCatFacts)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "cat_facts")
  }

  public override fun clearAllTables() {
    super.performClear(false, "cat_facts")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(CatFactDao::class, CatFactDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun catFactDao(): CatFactDao = _catFactDao.value
}
