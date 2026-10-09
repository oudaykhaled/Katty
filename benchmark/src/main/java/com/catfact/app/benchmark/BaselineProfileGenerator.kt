package com.catfact.app.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import java.util.regex.Pattern

/**
 * Baseline profile generator covering all critical user paths.
 *
 * Run separately via:
 *   ./gradlew :app:generateDevReleaseBaselineProfile
 *
 * The stable Baseline Profile plugin owns the non-minified, profileable target
 * and isolates profile generation from Macrobenchmark measurement rules.
 * Generated rules are saved per variant after collection; the authored main profile is retained until review.
 */
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        baselineProfileRule.collect(
            packageName = PACKAGE_NAME,
            maxIterations = PROFILE_ITERATIONS,
            includeInStartupProfile = true
        ) {
            // Startup
            pressHome()
            startActivityAndWait()
            check(device.wait(Until.hasObject(By.res("facts_list")), TIMEOUT_MS)) {
                "Facts list did not appear during profile generation"
            }
            check(device.wait(Until.hasObject(By.res(Pattern.compile("fact_card_.*"))), TIMEOUT_MS)) {
                "Profile generation requires loaded facts"
            }

            warmListScrolling()
            warmBookmarkToggle()
            warmSearch()
            warmDetailNavigation()
            warmTabNavigation()
        }
    }

    private fun MacrobenchmarkScope.warmListScrolling() {
        val list = device.findObject(By.res("facts_list")) ?: return
        list.setGestureMargin(device.displayWidth / GESTURE_MARGIN_DIVISOR)
        repeat(SCROLL_COUNT) {
            list.fling(Direction.DOWN)
            device.waitForIdle()
        }
        repeat(SCROLL_COUNT) {
            list.fling(Direction.UP)
            device.waitForIdle()
        }
    }

    private fun MacrobenchmarkScope.warmBookmarkToggle() {
        val bookmark = device.findObject(By.desc("Add to favorites")) ?: return
        bookmark.click()
        device.waitForIdle()
        device.findObject(By.desc("Remove from favorites"))?.click()
        device.waitForIdle()
    }

    private fun MacrobenchmarkScope.warmSearch() {
        val searchField = device.findObject(By.res("search_field")) ?: return
        searchField.click()
        device.waitForIdle()
        searchField.text = "cat"
        device.waitForIdle()
        Thread.sleep(SEARCH_SETTLE_MS)
        searchField.clear()
        device.waitForIdle()
    }

    private fun MacrobenchmarkScope.warmDetailNavigation() {
        val firstCard = device.findObject(By.res(Pattern.compile("fact_card_.*"))) ?: return
        firstCard.click()
        device.wait(Until.hasObject(By.res("detail_screen")), TIMEOUT_MS)
        device.waitForIdle()
        device.pressBack()
        device.wait(Until.hasObject(By.res("facts_list")), TIMEOUT_MS)
        device.waitForIdle()
    }

    private fun MacrobenchmarkScope.warmTabNavigation() {
        device.findObject(By.res("nav_favorites"))?.click()
        device.waitForIdle()
        Thread.sleep(TAB_SETTLE_MS)
        device.findObject(By.res("nav_facts"))?.click()
        device.waitForIdle()
    }

    private companion object {
        const val PACKAGE_NAME = "com.catfact.app"
        const val TIMEOUT_MS = 10_000L
        const val PROFILE_ITERATIONS = 3
        const val SCROLL_COUNT = 3
        const val GESTURE_MARGIN_DIVISOR = 5
        const val SEARCH_SETTLE_MS = 300L
        const val TAB_SETTLE_MS = 500L
    }
}
