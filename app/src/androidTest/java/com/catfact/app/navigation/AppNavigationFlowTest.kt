package com.catfact.app.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import com.catfact.app.MainActivity
import com.catfact.app.R
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.testing.FakeCatFactRepository
import com.catfact.app.core.testing.TestFixtures
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AppNavigationFlowTest {

    @Inject
    lateinit var repository: CatFactRepository

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        (repository as FakeCatFactRepository).setFacts(
            listOf(
                TestFixtures.createCatFact(id = FIRST_ID, fact = FIRST_TEXT),
                TestFixtures.createCatFact(id = SECOND_ID, fact = SECOND_TEXT, personalNote = SECOND_NOTE)
            )
        )
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

    @Test
    fun reselectFactsTab_fromDetail_returnsToFactsRoot() {
        openFact(FIRST_ID)

        composeRule.onNodeWithTag("nav_facts").performClick()

        composeRule.onNodeWithTag("detail_screen").assertDoesNotExist()
        composeRule.onNodeWithTag("facts_list").assertIsDisplayed()
    }

    @Test
    fun reselectFavoritesTab_fromDetail_returnsToFavoritesRoot() {
        openFact(FIRST_ID)
        composeRule.onNodeWithTag("detail_bookmark").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_favorites").performClick()
        composeRule.onNodeWithTag("fact_card_$FIRST_ID").performClick()
        assertDetailText(FIRST_TEXT)

        composeRule.onNodeWithTag("nav_favorites").performClick()

        composeRule.onNodeWithTag("detail_screen").assertDoesNotExist()
        composeRule.onNodeWithTag("favorites_list").assertIsDisplayed()
    }

    @Test
    fun openingDifferentFacts_keepsDetailViewModelsAndDraftsIsolated() {
        openFact(FIRST_ID)
        assertDetailText(FIRST_TEXT)
        composeRule.onNodeWithTag("note_editor").performTextReplacement("Unsaved draft for first fact")
        composeRule.onNodeWithTag("detail_back").performClick()

        openFact(SECOND_ID)
        assertDetailText(SECOND_TEXT)
        composeRule.onNodeWithTag("note_editor").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(SECOND_NOTE))
        )
    }

    @Test
    fun saveNoteAndReturnToDetail_displaysPersistedNote() {
        openFact(FIRST_ID)
        composeRule.onNodeWithTag("note_editor").performTextReplacement("Saved through the primary action")
        composeRule.onNodeWithTag("detail_save_note").performScrollTo().performClick()
        composeRule.waitUntil {
            composeRule.onAllNodesWithTag("detail_save_note").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithTag("detail_back").performClick()
        openFact(FIRST_ID)
        composeRule.onNodeWithTag("note_editor").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.EditableText,
                AnnotatedString("Saved through the primary action")
            )
        )
    }

    @Test
    fun bookmarkFromDetail_appearsInFavorites_andCanBeRemoved() {
        openFact(FIRST_ID)
        composeRule.onNodeWithTag("detail_bookmark").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("detail_back").performClick()
        composeRule.onNodeWithTag("nav_favorites").performClick()
        composeRule.onNodeWithTag("favorites_list").performScrollToNode(hasTestTag("fact_card_$FIRST_ID"))
        composeRule.onNodeWithTag("fact_card_$FIRST_ID").performClick()
        assertDetailText(FIRST_TEXT)
        composeRule.onNodeWithTag("detail_bookmark").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("detail_back").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("fact_card_$FIRST_ID").assertDoesNotExist()
    }

    @Test
    fun warmDeepLink_opensRequestedFact_andBackReturnsToPreviousDetail() {
        openFact(FIRST_ID)
        sendDeepLink(SECOND_ID)
        assertDetailText(SECOND_TEXT)
        composeRule.onNodeWithTag("detail_back").performClick()
        assertDetailText(FIRST_TEXT)
    }

    @Test
    fun consumedDeepLink_isNotReplayedAfterActivityRecreation() {
        sendDeepLink(SECOND_ID)
        assertDetailText(SECOND_TEXT)
        composeRule.activityRule.scenario.recreate()
        assertDetailText(SECOND_TEXT)
        composeRule.onNodeWithTag("detail_back").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("detail_screen").assertDoesNotExist()
        composeRule.onNodeWithTag("facts_list").assertIsDisplayed()
    }

    @Test
    fun unsavedNote_survivesActivityRecreationOnSameEntry() {
        openFact(FIRST_ID)
        composeRule.onNodeWithTag("note_editor").performTextReplacement("Draft across recreation")
        composeRule.activityRule.scenario.recreate()
        assertDetailText(FIRST_TEXT)
        composeRule.onNodeWithTag("note_editor").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("Draft across recreation"))
        )
    }

    private fun openFact(id: String) {
        composeRule.onNodeWithTag("facts_list").performScrollToNode(hasTestTag("fact_card_$id"))
        composeRule.onNodeWithTag("fact_card_$id").performClick()
        composeRule.waitUntil {
            composeRule.onAllNodesWithTag("detail_screen").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun assertDetailText(text: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("detail_fact_text").assertTextEquals(text)
    }

    private fun sendDeepLink(id: String) {
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("catfact://fact/$id"), activity, MainActivity::class.java)
            )
        }
        composeRule.waitForIdle()
    }

    private companion object {
        const val FIRST_ID = "navigation-first"
        const val FIRST_TEXT = "First navigation fixture fact."
        const val SECOND_ID = "navigation-second"
        const val SECOND_TEXT = "Second navigation fixture fact."
        const val SECOND_NOTE = "Note belonging to the second fact"
    }
}
