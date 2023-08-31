package com.msd.feature.main.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msd.feature.main.presenter.UserInteractions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class MainEmptyViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val userInteractions: UserInteractions = mock()

    @Test
    fun testEmptyViewIsDisplayedCorrectly() {
        composeTestRule.setContent {
            MainEmptyView(userInteractions)
        }

        val emptyListText = "You have no network configurations, add one!"
        composeTestRule.onNodeWithText(emptyListText).assertIsDisplayed()
        val actionText = "Configure a network location"
        composeTestRule.onNodeWithText(actionText).assertIsDisplayed()
    }

    @Test
    fun testEmptyViewActionIsWorkingCorrectly() {
        composeTestRule.setContent {
            MainEmptyView(userInteractions)
        }

        val actionText = "Configure a network location"
        composeTestRule.onNodeWithText(actionText).performClick()

        verify(userInteractions).onAddButtonClicked()
    }
}