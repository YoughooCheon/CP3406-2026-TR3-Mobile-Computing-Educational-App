package com.example.cp3406_assessment3_younghoocheon

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.NicknameDialog
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.theme.LocalReduceMotion
import com.example.cp3406_assessment3_younghoocheon.ui.theme.MemoryDeskTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** GUI tests for the reusable Compose components. They run on an emulator or device. */
class ComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pillButton_callsOnClickOnceWhenTapped() {
        var clicks = 0
        composeRule.setContent {
            MemoryDeskTheme { PillButton(text = "Get Started", onClick = { clicks += 1 }) }
        }

        composeRule.onNodeWithText("Get Started").assertIsEnabled().performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun tagChip_showsSelectedStateAndReportsTaps() {
        var taps = 0
        composeRule.setContent {
            MemoryDeskTheme {
                TagChip(text = "Privacy", selected = true, onClick = { taps += 1 })
                TagChip(text = "Access", selected = false, onClick = {})
            }
        }

        composeRule.onNodeWithText("Privacy").assertIsSelected()
        composeRule.onNodeWithText("Access").assertIsNotSelected()
        composeRule.onNodeWithText("Privacy").performClick()

        assertEquals(1, taps)
    }

    @Test
    fun progressBar_exposesItsValueToAccessibilityServices() {
        composeRule.setContent {
            MemoryDeskTheme {
                CompositionLocalProvider(LocalReduceMotion provides true) {
                    AppProgressBar(progress = 0.5f)
                }
            }
        }

        composeRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f))
    }

    @Test
    fun progressBar_clampsValuesAboveOne() {
        composeRule.setContent {
            MemoryDeskTheme {
                CompositionLocalProvider(LocalReduceMotion provides true) {
                    AppProgressBar(progress = 1.7f)
                }
            }
        }

        composeRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f))
    }

    @Test
    fun nicknameDialog_saveIsDisabledForABlankName() {
        composeRule.setContent {
            MemoryDeskTheme { NicknameDialog(current = "Student", onDismiss = {}, onSave = {}) }
        }
        composeRule.onNodeWithText("Save").assertIsEnabled()

        composeRule.onNode(hasSetTextAction()).performTextClearance()

        composeRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun nicknameDialog_savesTheNewNameAndRejectsCharactersBeyondTheLimit() {
        var saved: String? = null
        composeRule.setContent {
            MemoryDeskTheme { NicknameDialog(current = "Student", onDismiss = {}, onSave = { saved = it }) }
        }

        composeRule.onNode(hasSetTextAction()).performTextClearance()
        composeRule.onNode(hasSetTextAction()).performTextInput("A".repeat(20))
        composeRule.onNode(hasSetTextAction()).performTextInput("B")
        val counter = composeRule.onAllNodesWithText("20/20", substring = true).fetchSemanticsNodes()
        composeRule.onNodeWithText("Save").performClick()

        assertTrue(counter.isNotEmpty())
        assertEquals("A".repeat(20), saved)
    }
}
