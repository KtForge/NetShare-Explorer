package com.msd.feature.explorer.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class ExplorerLoadingViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingViewIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerLoadingView() }

        val contentDescription = "Loading your files, please wait"
        composeTestRule.onNodeWithContentDescription(contentDescription)
    }
}
