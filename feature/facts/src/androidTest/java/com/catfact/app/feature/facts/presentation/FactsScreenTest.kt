package com.catfact.app.feature.facts.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.catfact.app.core.designsystem.theme.CatFactTheme
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.facts.viewmodel.FactsEvent
import com.catfact.app.feature.facts.viewmodel.FactsUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FactsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun testFact(id: String = "1", text: String = "Cats sleep 16 hours a day") =
        CatFact(id = id, fact = text, length = text.length)

    @Test
    fun factsListDisplaysItems() {
        val facts = listOf(
            testFact("1", "Cats have 230 bones"),
            testFact("2", "A group of cats is called a clowder")
        ).toImmutableList()

        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(facts = facts),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("facts_list").assertIsDisplayed()
        composeRule.onNodeWithTag("fact_card_1").assertIsDisplayed()
        composeRule.onNodeWithTag("fact_card_2").assertIsDisplayed()
    }

    @Test
    fun emptyStateShowsSearchBarButNoCards() {
        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("facts_list").assertIsDisplayed()
        composeRule.onNodeWithTag("search_field").assertIsDisplayed()
        composeRule.onNodeWithTag("fact_card_1").assertDoesNotExist()
    }

    @Test
    fun errorBannerIsDisplayedAndDismissible() {
        val events = mutableListOf<FactsEvent>()

        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(error = UiText.Raw("Something went wrong")),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNodeWithTag("error_banner").assertIsDisplayed()
        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeRule.onNode(hasContentDescription("Dismiss error")).performClick()
        assertTrue(events.any { it is FactsEvent.DismissError })
    }

    @Test
    fun heroCardIsDisplayedWhenRandomFactPresent() {
        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(randomFact = testFact()),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("hero_card").assertIsDisplayed()
    }

    @Test
    fun factCardClickDispatchesFactClicked() {
        val events = mutableListOf<FactsEvent>()
        val facts = listOf(testFact("click-me", "Clickable fact")).toImmutableList()

        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(facts = facts),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNodeWithTag("fact_card_click-me").performClick()
        val clicked = events.filterIsInstance<FactsEvent.FactClicked>()
        assertEquals(1, clicked.size)
        assertEquals("click-me", clicked[0].factId)
    }

    @Test
    fun loadingMoreShowsProgressIndicator() {
        composeRule.setContent {
            CatFactTheme {
                FactsScreen(
                    state = FactsUiState(
                        facts = persistentListOf(testFact()),
                        isLoadingMore = true
                    ),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("facts_list").assertIsDisplayed()
    }
}
