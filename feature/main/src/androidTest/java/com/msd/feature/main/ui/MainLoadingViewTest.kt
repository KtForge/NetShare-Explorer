package com.msd.feature.main.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class MainLoadingViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingViewIsDisplayedCorrectly() {
        composeTestRule.setContent {
            MainLoadingView()
        }

        val contentDescription = "Loading your configurations, please wait"
        composeTestRule.onNodeWithContentDescription(contentDescription).assertIsDisplayed()
    }
}
