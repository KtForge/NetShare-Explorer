package com.msd.feature.edit

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.msd.feature.edit.ui.EditLoadingView
import org.junit.Rule
import org.junit.Test

class EditLoadingViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingViewIsDisplayedCorrectly() {
        composeTestRule.setContent { EditLoadingView() }

        val contentDescription = "Loading edit screen, please wait"
        composeTestRule.onNodeWithContentDescription(contentDescription)
    }
}
