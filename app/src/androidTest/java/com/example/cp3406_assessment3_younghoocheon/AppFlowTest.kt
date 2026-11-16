package com.example.cp3406_assessment3_younghoocheon

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Walks through the real app: introduction pages, bottom navigation and one flashcard session.
 * The app's own data is cleared first so every run starts from the same state.
 */
@RunWith(AndroidJUnit4::class)
class AppFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearAppData() {
        val container = (composeRule.activity.application as MemoryDeskApp).container
        runBlocking { container.repository.deleteAll() }
    }

    private fun waitForText(text: String, substring: Boolean = false) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForPage(description: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun openHome() {
        composeRule.onNodeWithText("Skip").performClick()
        waitForText("Hello,", substring = true)
    }

    @Test
    fun introduction_hasThreePagesAndTheLastOneStartsTheApp() {
        waitForPage("Page 1 of 3")
        composeRule.onNodeWithText("Next").performClick()
        waitForPage("Page 2 of 3")
        composeRule.onNodeWithText("Next").performClick()
        waitForPage("Page 3 of 3")

        composeRule.onNodeWithText("Get Started").performClick()

        waitForText("Hello,", substring = true)
        composeRule.onNodeWithText("My decks").assertExists()
    }

    @Test
    fun skipButton_goesStraightToTheHomeScreen() {
        openHome()

        composeRule.onNodeWithText("My decks").assertExists()
    }

    @Test
    fun bottomBar_opensTheProgressScreen() {
        openHome()

        composeRule.onNodeWithContentDescription("Progress").performClick()

        composeRule.onNodeWithText("STUDY DAYS IN A ROW").assertExists()
    }

    @Test
    fun practiceHub_offersBothActivities() {
        openHome()

        composeRule.onNodeWithContentDescription("Practice").performClick()

        waitForText("Start flashcards")
        composeRule.onNodeWithText("Start word builder").assertExists()
    }

    @Test
    fun flashcardSession_revealsAnswerThenMovesToTheNextCard() {
        openHome()
        composeRule.onNodeWithContentDescription("Practice").performClick()
        waitForText("Start flashcards")
        composeRule.onNodeWithText("Start flashcards").performClick()
        waitForText("Show answer")

        composeRule.onNodeWithText("Show answer").performClick()
        composeRule.onNodeWithText("Good").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasText("Card 2 of", substring = true) or hasText("Done"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}