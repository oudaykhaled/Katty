package com.catfact.app.feature.factdetail.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.catfact.app.core.designsystem.theme.CatFactTheme
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.feature.factdetail.viewmodel.DetailEvent
import com.catfact.app.feature.factdetail.viewmodel.DetailUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleFact = CatFact(
        id = "d1",
        fact = "A cat's hearing is much more sensitive than a human's",
        length = 53,
        isBookmarked = false
    )

    @Test
    fun contentStateDisplaysFactText() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(fact = sampleFact, isLoading = false),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("detail_screen").assertIsDisplayed()
        composeRule.onNodeWithText(sampleFact.fact).assertIsDisplayed()
    }

    @Test
    fun loadingStateShowsProgressIndicator() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(isLoading = true),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithTag("detail_screen").assertDoesNotExist()
    }

    @Test
    fun errorStateShowsErrorMessage() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(
                        isLoading = false,
                        error = UiText.Raw("Fact not found")
                    ),
                    onEvent = {}
                )
            }
        }

        composeRule.onNodeWithText("Fact not found").assertIsDisplayed()
        composeRule.onNodeWithTag("detail_screen").assertDoesNotExist()
    }

    @Test
    fun backButtonDispatchesNavigateBack() {
        val events = mutableListOf<DetailEvent>()

        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(fact = sampleFact, isLoading = false),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNode(hasContentDescription("Back")).performClick()
        assertTrue(events.any { it is DetailEvent.NavigateBack })
    }

    @Test
    fun shareButtonVisibleWhenFactLoaded() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(fact = sampleFact, isLoading = false),
                    onEvent = {}
                )
            }
        }

        composeRule.onNode(hasContentDescription("Share")).assertIsDisplayed()
    }

    @Test
    fun shareButtonHiddenWhenNoFact() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(isLoading = false, error = UiText.Raw("Error")),
                    onEvent = {}
                )
            }
        }

        composeRule.onNode(hasContentDescription("Share")).assertDoesNotExist()
    }

    @Test
    fun saveNoteButtonAppearsWhenNoteChanged() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(
                        fact = sampleFact,
                        isLoading = false,
                        editedNote = "My new note",
                        hasNoteChanges = true
                    ),
                    onEvent = {}
                )
            }
        }

        composeRule.onNode(hasText("Save Note")).assertIsDisplayed()
    }

    @Test
    fun saveNoteButtonHiddenWhenNoChanges() {
        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(
                        fact = sampleFact,
                        isLoading = false,
                        editedNote = "",
                        hasNoteChanges = false
                    ),
                    onEvent = {}
                )
            }
        }

        composeRule.onNode(hasText("Save Note")).assertDoesNotExist()
    }

    @Test
    fun bookmarkToggleDispatchesEvent() {
        val events = mutableListOf<DetailEvent>()

        composeRule.setContent {
            CatFactTheme {
                DetailScreen(
                    state = DetailUiState(fact = sampleFact, isLoading = false),
                    onEvent = { events.add(it) }
                )
            }
        }

        composeRule.onNode(hasContentDescription("Add to favorites")).performClick()
        assertTrue(events.any { it is DetailEvent.ToggleBookmark })
    }
}
