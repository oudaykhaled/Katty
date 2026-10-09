package com.catfact.app.core.database.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.catfact.app.core.database.CatFactDatabase
import com.catfact.app.core.database.entity.CatFactEntity
import com.catfact.app.core.database.mapper.toEntity
import com.catfact.app.core.domain.model.ServerException
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.network.api.CatFactApi
import com.catfact.app.core.network.model.CatFactListResponse
import com.catfact.app.core.network.model.CatFactResponse
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.UUID

/** Exercises the generated Room DAO and SQLite, including closing and reopening the database. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class CatFactPersistenceTest {

    private lateinit var context: Context
    private lateinit var database: CatFactDatabase
    private lateinit var databaseName: String
    private lateinit var api: FakeCatFactApi
    private lateinit var repository: CatFactRepositoryImpl
    private val logger: Logger = mockk(relaxed = true)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        databaseName = "persistence-${UUID.randomUUID()}.db"
        database = openDatabase()
        api = FakeCatFactApi()
        repository = newRepository()
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun `page one refresh retains bookmarked and noted facts even when absent from the response`() = runTest {
        api.facts = listOf(BOOKMARKED, NOTED, DISPOSABLE)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(NOTED.id, "Keep this locally")

        api.facts = listOf(NEW_FACT)
        repository.refreshFacts(1, 20)

        assertTrue(repository.getFactById(BOOKMARKED.id)!!.isBookmarked)
        assertEquals("Keep this locally", repository.getFactById(NOTED.id)!!.personalNote)
        assertEquals(DISPOSABLE.fact, repository.getFactById(DISPOSABLE.id)!!.fact)
        assertEquals(setOf(NEW_FACT.id), storedIds())
        assertEquals(listOf(BOOKMARKED.id), repository.observeFavorites().first().map { it.id })
    }

    @Test
    fun `repeated refresh and overlapping pagination retain annotations while updating remote metadata`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(BOOKMARKED.id, "My note")

        repeat(3) {
            api.facts = listOf(BOOKMARKED.copy(length = 99), NEW_FACT)
            val result = repository.refreshFacts(page = if (it == 1) 2 else 1, limit = 20)
            val refreshed = result.facts.first { fact -> fact.id == BOOKMARKED.id }
            assertTrue(refreshed.isBookmarked)
            assertEquals("My note", refreshed.personalNote)
            assertEquals(99, refreshed.length)
        }

        assertEquals(2, repository.observeFacts().first().size)
        assertEquals("My note", repository.getFactById(BOOKMARKED.id)!!.personalNote)
    }

    @Test
    fun `annotations survive repository recreation and reopening the on disk database`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(BOOKMARKED.id, "Survives process recreation")

        database.close()
        database = openDatabase()
        repository = newRepository()
        repository.refreshFacts(1, 20)

        val fact = repository.getFactById(BOOKMARKED.id)!!
        assertTrue(fact.isBookmarked)
        assertEquals("Survives process recreation", fact.personalNote)
    }

    @Test
    fun `random endpoint and list endpoint share one identity and preserve annotations`() = runTest {
        api.randomFact = BOOKMARKED
        val first = repository.fetchRandomFact()
        repository.toggleBookmark(first.id, false)
        repository.updateNote(first.id, "Random favorite")

        assertTrue(repository.observeFacts().first().isEmpty())
        val repeated = repository.fetchRandomFact()
        api.facts = listOf(BOOKMARKED)
        val listed = repository.refreshFacts(1, 20).facts.single()

        assertEquals(first.id, repeated.id)
        assertEquals(first.id, listed.id)
        assertTrue(repeated.isBookmarked)
        assertTrue(listed.isBookmarked)
        assertEquals("Random favorite", repeated.personalNote)
        assertEquals("Random favorite", listed.personalNote)
        assertEquals(1, repository.observeFacts().first().size)
    }

    @Test
    fun `empty remote refresh clears feed membership and preserves addressable cache`() = runTest {
        api.facts = listOf(BOOKMARKED, NOTED, DISPOSABLE)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(NOTED.id, "Note without bookmark")

        api.facts = emptyList()
        assertTrue(repository.refreshFacts(1, 20).facts.isEmpty())

        assertTrue(storedIds().isEmpty())
        assertTrue(repository.getFactById(BOOKMARKED.id)!!.isBookmarked)
        assertEquals("Note without bookmark", repository.getFactById(NOTED.id)!!.personalNote)
        assertEquals(DISPOSABLE.fact, repository.getFactById(DISPOSABLE.id)!!.fact)
    }

    @Test
    fun `failed network refresh leaves cached facts and local edits intact`() = runTest {
        api.facts = listOf(BOOKMARKED, DISPOSABLE)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(BOOKMARKED.id, "Keep after failure")
        val before = repository.observeFacts().first()

        api.failure = IOException("Offline")
        val failure = runCatching { repository.refreshFacts(1, 20) }.exceptionOrNull()

        assertTrue(failure is IOException)
        assertEquals(before, repository.observeFacts().first())
    }

    @Test
    fun `HTTP failure retains the cache and exposes the server status`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        api.failure = HttpException(Response.error<Any>(503, "Unavailable".toResponseBody()))

        val failure = runCatching { repository.refreshFacts(1, 20) }.exceptionOrNull()

        assertTrue(failure is ServerException)
        assertEquals(503, (failure as ServerException).code)
        assertEquals(setOf(BOOKMARKED.id), storedIds())
    }

    @Test
    fun `local edit made during network refresh is preserved when its response arrives`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        val requestStarted = CompletableDeferred<Unit>()
        val releaseResponse = CompletableDeferred<Unit>()
        api.beforeResponse = {
            requestStarted.complete(Unit)
            releaseResponse.await()
        }
        val refresh = async { repository.refreshFacts(1, 20) }
        requestStarted.await()

        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(BOOKMARKED.id, "Edited while loading")
        releaseResponse.complete(Unit)
        refresh.await()

        assertTrue(repository.getFactById(BOOKMARKED.id)!!.isBookmarked)
        assertEquals("Edited while loading", repository.getFactById(BOOKMARKED.id)!!.personalNote)
    }

    @Test
    fun `refresh and local edits retain legacy pending and failed records`() = runTest {
        val dao = database.catFactDao()
        val pending = CatFactEntity("pending", "Original pending", 16, syncStatus = "PENDING")
        val failed = CatFactEntity("failed", "Original failed", 15, syncStatus = "FAILED")
        dao.insertFacts(listOf(pending, failed))
        repository.toggleBookmark(pending.id, false)
        repository.updateNote(failed.id, "Keep failed record")

        dao.replaceAllFacts(listOf(pending.copy(fact = "Remote overwrite", syncStatus = "SYNCED")))

        assertEquals("Original pending", dao.getFactById(pending.id)!!.fact)
        assertEquals(SyncStatus.PENDING, repository.getFactById(pending.id)!!.syncStatus)
        assertEquals(SyncStatus.FAILED, repository.getFactById(failed.id)!!.syncStatus)
        assertEquals("Keep failed record", repository.getFactById(failed.id)!!.personalNote)
    }

    @Test
    fun `inactive facts remain addressable after annotations are cleared`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        repository.toggleBookmark(BOOKMARKED.id, false)
        repository.updateNote(BOOKMARKED.id, "Temporary")
        repository.toggleBookmark(BOOKMARKED.id, true)
        repository.updateNote(BOOKMARKED.id, "")
        assertFalse(repository.getFactById(BOOKMARKED.id)!!.isBookmarked)

        api.facts = emptyList()
        repository.refreshFacts(1, 20)

        assertTrue(storedIds().isEmpty())
        assertEquals("", repository.getFactById(BOOKMARKED.id)!!.personalNote)
    }

    @Test
    fun `invalid page arguments fail before network or cache changes`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 20)
        val before = repository.observeFacts().first()
        val requestsBefore = api.requests

        assertTrue(runCatching { repository.refreshFacts(0, 20) }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.refreshFacts(1, 0) }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.refreshCachedFacts(0) }.exceptionOrNull() is IllegalArgumentException)

        assertEquals(requestsBefore, api.requests)
        assertEquals(before, repository.observeFacts().first())
    }

    @Test
    fun `later pages append in server order and random fetch does not move the feed`() = runTest {
        api.facts = listOf(BOOKMARKED, NOTED)
        repository.refreshFacts(1, 2)
        api.facts = listOf(DISPOSABLE, NEW_FACT)
        repository.refreshFacts(2, 2)
        val expected = listOf(BOOKMARKED.id, NOTED.id, DISPOSABLE.id, NEW_FACT.id)
        assertEquals(expected, repository.observeFacts().first().map { it.id })

        api.randomFact = NOTED
        repository.fetchRandomFact()
        api.randomFact = CatFactResponse("A random fact outside the feed.", 31)
        val random = repository.fetchRandomFact()

        assertEquals(expected, repository.observeFacts().first().map { it.id })
        assertEquals(random, repository.getFactById(random.id))
        assertEquals(listOf(0L, 1L, 2L, 3L), database.catFactDao().observeAllFacts().first().map { it.feedPosition })
    }

    @Test
    fun `overlapping later page preserves first occurrence and refresh replaces only active feed`() = runTest {
        api.facts = listOf(BOOKMARKED, NOTED)
        repository.refreshFacts(1, 2)
        api.facts = listOf(NOTED, DISPOSABLE)
        repository.refreshFacts(2, 2)
        assertEquals(listOf(BOOKMARKED.id, NOTED.id, DISPOSABLE.id), repository.observeFacts().first().map { it.id })

        api.facts = listOf(DISPOSABLE, BOOKMARKED)
        repository.refreshFacts(1, 2)

        assertEquals(listOf(DISPOSABLE.id, BOOKMARKED.id), repository.observeFacts().first().map { it.id })
        assertEquals(NOTED.fact, repository.getFactById(NOTED.id)!!.fact)
        assertNull(database.catFactDao().getFactById(NOTED.id)!!.feedPosition)
    }

    @Test
    fun `fact opened from a later page remains editable after background first page refresh`() = runTest {
        api.facts = listOf(BOOKMARKED)
        repository.refreshFacts(1, 1)
        api.facts = listOf(NOTED)
        val openedFact = repository.refreshFacts(2, 1).facts.single()
        api.facts = listOf(NEW_FACT)
        repository.refreshFacts(1, 1)

        repository.updateNote(openedFact.id, "Written after background refresh")
        repository.toggleBookmark(openedFact.id, openedFact.isBookmarked)

        val saved = repository.getFactById(openedFact.id)!!
        assertEquals("Written after background refresh", saved.personalNote)
        assertTrue(saved.isBookmarked)
        assertEquals(listOf(openedFact.id), repository.observeFavorites().first().map { it.id })
        assertEquals(listOf(NEW_FACT.id), repository.observeFacts().first().map { it.id })
    }

    @Test
    fun `editing an unknown fact fails instead of reporting a successful save`() = runTest {
        assertTrue(runCatching { repository.updateNote("missing", "Note") }.exceptionOrNull() is IllegalStateException)
        val bookmarkFailure = runCatching { repository.toggleBookmark("missing", false) }.exceptionOrNull()
        assertTrue(bookmarkFailure is IllegalStateException)
        assertNull(repository.getFactById("missing"))
    }

    @Test
    fun `background cache refresh preserves four interactive pages and the next page has no gap`() = runTest {
        val interactiveFacts = (0 until 100).map { CatFactResponse("Ordered cat fact $it", 20) }
        repeat(4) { pageIndex ->
            api.facts = interactiveFacts.drop(pageIndex * 20).take(20)
            repository.refreshFacts(pageIndex + 1, 20)
        }
        repository.toggleBookmark(interactiveFacts[10].id, false)
        repository.updateNote(interactiveFacts[60].id, "Detail from page four")
        val expectedBefore = interactiveFacts.take(80).map { it.id }
        val positionsBefore = database.catFactDao().observeAllFacts().first().map { it.feedPosition }
        val cacheOnlyFact = CatFactResponse("Background-only cat fact", 24)
        api.facts = interactiveFacts.take(49).map { it.copy(length = 99) } + cacheOnlyFact

        val cached = repository.refreshCachedFacts(50)

        assertEquals(50, cached.size)
        assertTrue(cached.first { it.id == interactiveFacts[10].id }.isBookmarked)
        assertEquals(expectedBefore, repository.observeFacts().first().map { it.id })
        assertEquals(positionsBefore, database.catFactDao().observeAllFacts().first().map { it.feedPosition })
        assertEquals(99, repository.getFactById(interactiveFacts[0].id)!!.length)
        assertEquals("Detail from page four", repository.getFactById(interactiveFacts[60].id)!!.personalNote)
        assertNull(database.catFactDao().getFactById(cacheOnlyFact.id)!!.feedPosition)

        api.facts = interactiveFacts.drop(80).take(20)
        repository.refreshFacts(5, 20)

        assertEquals(interactiveFacts.map { it.id }, repository.observeFacts().first().map { it.id })
    }

    @Test
    fun `version one migration preserves every stored field and the offline feed order`() = runTest {
        database.close()
        val legacy = listOf(
            CatFactEntity("b", "Bookmarked legacy", 17, true, "Mijn notitie 🐈", "FAILED", 200L),
            CatFactEntity("a", "Pending legacy", 14, false, "Keep note", "PENDING", 200L),
            CatFactEntity("c", "Cached legacy", 13, false, "", "SYNCED", 100L)
        )
        createVersionOneDatabase(legacy)
        database = openDatabase()
        repository = newRepository()

        val migrated = database.catFactDao().observeAllFacts().first()
        assertEquals(listOf("a", "b", "c"), migrated.map { it.id })
        assertEquals(listOf(0L, 1L, 2L), migrated.map { it.feedPosition })
        legacy.forEach { original ->
            assertEquals(original, database.catFactDao().getFactById(original.id)!!.copy(feedPosition = null))
        }
        assertEquals(2, database.openHelper.readableDatabase.version)
        assertEquals(listOf("b"), repository.observeFavorites().first().map { it.id })

        api.facts = listOf(NEW_FACT)
        repository.refreshFacts(1, 20)
        assertEquals(listOf(NEW_FACT.id), repository.observeFacts().first().map { it.id })
        assertEquals("Mijn notitie 🐈", repository.getFactById("b")!!.personalNote)
        assertEquals("Keep note", repository.getFactById("a")!!.personalNote)
    }

    private fun createVersionOneDatabase(facts: List<CatFactEntity>) {
        context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null).use { sqlite ->
            // Exact table and identity from the checked-in version-one Room schema.
            sqlite.execSQL(
                """
                CREATE TABLE cat_facts (
                    id TEXT NOT NULL PRIMARY KEY, fact TEXT NOT NULL, length INTEGER NOT NULL,
                    isBookmarked INTEGER NOT NULL, personalNote TEXT NOT NULL,
                    syncStatus TEXT NOT NULL, cachedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            sqlite.execSQL("CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
            sqlite.execSQL("INSERT INTO room_master_table VALUES (42, 'aed7efdb5a021340df43feea162d85be')")
            facts.forEach { fact ->
                sqlite.execSQL(
                    "INSERT INTO cat_facts VALUES (?, ?, ?, ?, ?, ?, ?)",
                    arrayOf<Any>(fact.id, fact.fact, fact.length, if (fact.isBookmarked) 1 else 0,
                        fact.personalNote, fact.syncStatus, fact.cachedAt)
                )
            }
            sqlite.version = 1
        }
    }

    private fun openDatabase(): CatFactDatabase =
        Room.databaseBuilder(context, CatFactDatabase::class.java, databaseName)
            .addMigrations(CatFactDatabase.MIGRATION_1_2)
            .build()

    private fun newRepository() = CatFactRepositoryImpl(database.catFactDao(), api, logger)

    private suspend fun storedIds(): Set<String> = repository.observeFacts().first().map { it.id }.toSet()

    private class FakeCatFactApi : CatFactApi {
        var facts: List<CatFactResponse> = emptyList()
        var randomFact: CatFactResponse = BOOKMARKED
        var failure: Exception? = null
        var beforeResponse: suspend () -> Unit = {}
        var requests: Int = 0

        override suspend fun getRandomFact(): CatFactResponse = randomFact

        override suspend fun getFacts(page: Int, limit: Int): CatFactListResponse {
            requests++
            beforeResponse()
            failure?.let { throw it }
            return CatFactListResponse(page, facts, lastPage = maxOf(page, 2), total = facts.size)
        }
    }

    private val CatFactResponse.id: String get() = toEntity().id

    companion object {
        private val BOOKMARKED = CatFactResponse("Cats recognize familiar voices.", 31)
        private val NOTED = CatFactResponse("Cats can rotate their ears.", 27)
        private val DISPOSABLE = CatFactResponse("Cats sleep for many hours.", 26)
        private val NEW_FACT = CatFactResponse("Cats have whiskers.", 19)
    }
}
