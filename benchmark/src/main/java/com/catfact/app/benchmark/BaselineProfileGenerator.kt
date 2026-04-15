package com.catfact.app.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.regex.Pattern

/**
 * Baseline profile generator covering all critical user paths.
 *
 * Run separately via:
 *   ./gradlew :app:generateBaselineProfile
 *
 * @Ignore'd because the `androidx.baselineprofile` producer plugin is
 * incompatible with AGP 9 (TestExtension API removed). Without the plugin
 * orchestrating dex2oat, [BaselineProfileRule.collect] runs as a plain
 * connected test whose heavy compilation can OOM-kill the instrumentation
 * process. Re-enable when the baselineprofile plugin ships AGP 9 support.
 */
@Ignore("baselineprofile plugin incompatible with AGP 9 — collect() OOM-kills the instrumentation process")
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        baselineProfileRule.collect(
            packageName = PACKAGE_NAME,
            maxIterations = 3,
            includeInStartupProfile = true
        ) {
            // Startup
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.res("facts_list")), TIMEOUT_MS)

            // Scroll facts list to warm LazyColumn item composition paths
            val list = device.findObject(By.res("facts_list"))
            if (list != null) {
                list.setGestureMargin(device.displayWidth / 5)
                repeat(3) {
                    list.fling(Direction.DOWN)
                    device.waitForIdle()
                }
                repeat(3) {
                    list.fling(Direction.UP)
                    device.waitForIdle()
                }
            }

            // Bookmark toggle to warm animation + database write paths
            val bookmark = device.findObject(By.desc("Add to favorites"))
            if (bookmark != null) {
                bookmark.click()
                device.waitForIdle()
                val removeBookmark = device.findObject(By.desc("Remove from favorites"))
                removeBookmark?.click()
                device.waitForIdle()
            }

            // Search to warm filter + recomposition paths
            val searchField = device.findObject(By.res("search_field"))
            if (searchField != null) {
                searchField.click()
                device.waitForIdle()
                searchField.text = "cat"
                device.waitForIdle()
                Thread.sleep(300)
                searchField.clear()
                device.waitForIdle()
            }

            // Navigate to detail to warm detail screen composition
            val firstCard = device.findObject(By.res(Pattern.compile("fact_card_.*")))
            if (firstCard != null) {
                firstCard.click()
                device.wait(Until.hasObject(By.res("detail_screen")), TIMEOUT_MS)
                device.waitForIdle()
                device.pressBack()
                device.wait(Until.hasObject(By.res("facts_list")), TIMEOUT_MS)
                device.waitForIdle()
            }

            // Navigate to Favorites tab
            val favTab = device.findObject(By.res("nav_favorites"))
            if (favTab != null) {
                favTab.click()
                device.waitForIdle()
                Thread.sleep(500)
            }

            // Navigate back to Facts tab
            val factsTab = device.findObject(By.res("nav_facts"))
            if (factsTab != null) {
                factsTab.click()
                device.waitForIdle()
            }
        }
    }

    private companion object {
        const val PACKAGE_NAME = "com.catfact.app"
        const val TIMEOUT_MS = 10_000L
    }
}
