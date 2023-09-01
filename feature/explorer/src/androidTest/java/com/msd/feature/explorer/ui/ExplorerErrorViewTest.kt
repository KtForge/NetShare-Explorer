package com.msd.feature.explorer.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.msd.feature.explorer.presenter.ExplorerState.Error.AccessError
import com.msd.feature.explorer.presenter.ExplorerState.Error.ConnectionError
import com.msd.feature.explorer.presenter.ExplorerState.Error.UnknownError
import org.junit.Rule
import org.junit.Test

class ExplorerErrorViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testConnectionErrorViewIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerErrorView(error = ConnectionError("Name")) }

        val errorMessage = "Shared folder can't be accessed. Something happened when connecting to the server."
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun testAccessErrorViewIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerErrorView(error = AccessError("Name")) }

        val errorMessage = "Access denied. Please check your credentials."
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun testUnknownErrorViewIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerErrorView(error = UnknownError("Name")) }

        val errorMessage = "Shared folder can't be accessed. Something happened when connecting to the server."
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
