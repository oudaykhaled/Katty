package com.catfact.app.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.regex.Pattern

/**
 * Macrobenchmarks for the CatFact app covering startup, scroll, interaction,
 * navigation, and idle-recomposition scenarios.
 *
 * Run with:
 *   ./gradlew :benchmark:connectedDevBenchmarkAndroidTest
 *
 * After each test, Macrobenchmark writes a .perfetto-trace file. Open in
 * [Perfetto UI](https://ui.perfetto.dev/) and look for:
 * - **Choreographer#doFrame** slices > 16 ms to spot jank
 * - **Compose:recompose** depth to verify stability config effectiveness
 * - **measure / layout** during list scroll and tab transitions
 *
 * Each test isolates a user journey so regressions are attributable to a
 * specific interaction pattern.
 */
class FactsBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    // ── Startup ──────────────────────────────────────────────────────────────

    @Test
    fun coldStartup() = startup(StartupMode.COLD, CompilationMode.DEFAULT)

    @Test
    fun warmStartup() = startup(StartupMode.WARM, CompilationMode.DEFAULT)

    @Test
    fun hotStartup() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()
    }

    // ── Startup — compilation mode comparison ────────────────────────────────
    // These quantify the benefit of baseline profiles by comparing startup
    // latency across dex compilation strategies. The Full() and Partial() modes
    // require `cmd package compile` to succeed on the device; some OEM ROMs
    // (e.g. Samsung One UI) may fail — @Ignore the affected tests if needed.

    @Test
    fun coldStartupNoCompilation() = startup(StartupMode.COLD, CompilationMode.None())

    @Test
    @Ignore("CompilationMode.Partial requires baseline profile broadcast; disabled until profiles are generated")
    fun coldStartupPartialCompilation() = startup(
        StartupMode.COLD,
        CompilationMode.Partial(warmupIterations = 3)
    )

    @Test
    fun coldStartupFullCompilation() = startup(StartupMode.COLD, CompilationMode.Full())

    private fun startup(startupMode: StartupMode, compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = ITERATIONS,
            startupMode = startupMode,
            compilationMode = compilationMode
        ) {
            pressHome()
            startActivityAndWait()
            waitForContent()
        }
    }

    // ── Scroll ───────────────────────────────────────────────────────────────

    @Test
    fun scrollFactsList() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()
        scrollListDownAndUp()
    }

    /**
     * Waits 2 seconds after data loads before scrolling, measuring the
     * idle-to-scroll transition and any lingering recomposition cost.
     */
    @Test
    fun scrollAfterDataLoads() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()
        Thread.sleep(2_000)
        scrollListDownAndUp()
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    @Test
    @Ignore("Bookmark toggle crashes instrumentation on Samsung One UI; investigate StaleObjectException in CI")
    fun bookmarkToggle() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()
        toggleBookmark(times = 1)
    }

    @Test
    fun pullToRefresh() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()

        val list = device.findObject(By.res("facts_list")) ?: return@measureRepeated
        list.setGestureMargin(device.displayWidth / 5)
        list.fling(Direction.UP)
        device.waitForIdle()
        Thread.sleep(1_000)
    }

    @Test
    fun searchInteraction() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()

        val searchField = device.findObject(By.res("search_field"))
        if (searchField != null) {
            searchField.click()
            device.waitForIdle()
            searchField.text = "cat"
            device.waitForIdle()
            Thread.sleep(500)
            searchField.clear()
            device.waitForIdle()
            Thread.sleep(500)
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Test
    fun tabSwitching() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()

        navigateToFavorites()
        Thread.sleep(500)
        navigateToFacts()
        Thread.sleep(500)
        navigateToFavorites()
        Thread.sleep(500)
        navigateToFacts()
        device.waitForIdle()
    }

    @Test
    fun navigateToDetailAndBack() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()

        val firstCard = device.findObject(By.res(Pattern.compile("fact_card_.*")))
        if (firstCard != null) {
            firstCard.click()
            device.wait(Until.hasObject(By.res("detail_screen")), CONTENT_TIMEOUT_MS)
            device.waitForIdle()
            Thread.sleep(500)
            device.pressBack()
            device.wait(Until.hasObject(By.res("facts_list")), CONTENT_TIMEOUT_MS)
            device.waitForIdle()
        }
    }

    // ── Idle recomposition ───────────────────────────────────────────────────

    /**
     * Captures frame metrics while the app is idle for 5 seconds after content
     * loads. The LazyColumn subtree should stay stable with no unexpected
     * recompositions. In Perfetto, Compose:recompose depth should be minimal.
     */
    @Test
    fun idleFrameStability() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        waitForContent()
        Thread.sleep(5_000)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun MacrobenchmarkScope.waitForContent() {
        device.wait(Until.hasObject(By.res("facts_list")), CONTENT_TIMEOUT_MS)
    }

    private fun MacrobenchmarkScope.scrollListDownAndUp() {
        val list = device.findObject(By.res("facts_list")) ?: return
        val margin = device.displayWidth / 5
        list.setGestureMargin(margin)

        repeat(5) {
            list.fling(Direction.DOWN)
            device.waitForIdle()
        }
        repeat(3) {
            list.fling(Direction.UP)
            device.waitForIdle()
        }
    }

    private fun MacrobenchmarkScope.toggleBookmark(times: Int) {
        repeat(times) {
            Thread.sleep(500)
            device.waitForIdle()
            val addBookmark = device.findObject(By.desc("Add to favorites"))
            if (addBookmark != null) {
                addBookmark.click()
                Thread.sleep(800)
                device.waitForIdle()
                return@repeat
            }
            val removeBookmark = device.findObject(By.desc("Remove from favorites"))
            if (removeBookmark != null) {
                removeBookmark.click()
                Thread.sleep(800)
                device.waitForIdle()
            }
        }
    }

    private fun MacrobenchmarkScope.navigateToFavorites() {
        val tab = device.findObject(By.res("nav_favorites"))
        tab?.click()
        device.waitForIdle()
    }

    private fun MacrobenchmarkScope.navigateToFacts() {
        val tab = device.findObject(By.res("nav_facts"))
        tab?.click()
        device.waitForIdle()
    }

    private companion object {
        const val PACKAGE_NAME = "com.catfact.app"
        const val CONTENT_TIMEOUT_MS = 10_000L
        const val ITERATIONS = 3
    }
}
