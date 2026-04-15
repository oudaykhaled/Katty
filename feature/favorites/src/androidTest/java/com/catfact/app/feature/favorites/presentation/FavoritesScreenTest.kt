package com.catfact.app.feature.favorites.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.catfact.app.core.designsystem.theme.CatFactTheme
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.favorites.viewmodel.FavoritesEvent
import com.catfact.app.feature.favorites.viewmodel.FavoritesUiState
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun bookmarkedFact(id: String = "1", text: String = "Cats can rotate their ears 180 degrees") =
        CatFact(id = id, fact = text, length = text.length, isBookmarked = true)

    @Test
    fun emptyStateShowsEmptyMessage() {
        composeRule.setContent {
            CatFactTheme {
                FavoritesScreen(
                    state = FavoritesUiState(),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithText("No favorites yet").assertIsDisplayed()
        composeRule.onNodeWithText("Tap the heart on any fact to save it here.").assertIsDisplayed()
    }

    @Test
    fun populatedStateShowsFavoriteCards() {
        val favs = listOf(
            bookmarkedFact("f1", "Cats have five toes on front paws"),
            bookmarkedFact("f2", "A cat can jump up to six times its length")
        ).toImmutableList()

        composeRule.setContent {
            CatFactTheme {
                FavoritesScreen(
                    state = FavoritesUiState(favorites = favs),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("favorites_list").assertIsDisplayed()
        composeRule.onNodeWithTag("fact_card_f1").assertIsDisplayed()
        composeRule.onNodeWithTag("fact_card_f2").assertIsDisplayed()
    }

    @Test
    fun factCardClickDispatchesFactClicked() {
        val events = mutableListOf<FavoritesEvent>()
        val favs = listOf(bookmarkedFact("tap-me")).toImmutableList()

        composeRule.setContent {
            CatFactTheme {
                FavoritesScreen(
                    state = FavoritesUiState(favorites = favs),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNodeWithTag("fact_card_tap-me").performClick()
        val clicked = events.filterIsInstance<FavoritesEvent.FactClicked>()
        assertEquals(1, clicked.size)
        assertEquals("tap-me", clicked[0].factId)
    }

    @Test
    fun errorBannerAppearsAndDismissWorks() {
        val events = mutableListOf<FavoritesEvent>()

        composeRule.setContent {
            CatFactTheme {
                FavoritesScreen(
                    state = FavoritesUiState(error = UiText.Raw("Load failed")),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNodeWithTag("error_banner").assertIsDisplayed()
        composeRule.onNodeWithText("Load failed").assertIsDisplayed()

        composeRule.onNode(hasContentDescription("Dismiss error")).performClick()
        assertTrue(events.any { it is FavoritesEvent.DismissError })
    }
}
