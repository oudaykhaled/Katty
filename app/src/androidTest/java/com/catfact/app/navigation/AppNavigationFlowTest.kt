package com.catfact.app.navigation

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.catfact.app.MainActivity
import com.catfact.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AppNavigationFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun getString(resId: Int): String =
        composeRule.activity.getString(resId)

    @Test
    fun hiltGraphResolvesAndActivityLaunches() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("bottom_navigation_bar").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_displaysFactsTab() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_facts").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_displaysFavoritesTab() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_favorites").assertIsDisplayed()
    }

    @Test
    fun navigateToFavorites_showsFavoritesScreen() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_favorites").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText(getString(R.string.nav_label_favorites)).assertCountEquals(2)
    }

    @Test
    fun navigateBackToFacts_showsFactsScreen() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_favorites").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_facts").performClick()
        composeRule.waitForIdle()
        val factsTitle = composeRule.activity.getString(com.catfact.app.feature.facts.R.string.facts_title)
        composeRule.onNodeWithText(factsTitle).assertIsDisplayed()
    }
}
