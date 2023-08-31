package com.msd.feature.main.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.presenter.MainState.Loaded
import com.msd.feature.main.presenter.UserInteractions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class MainLoadedViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loaded = Loaded(
        smbConfigurations = listOf(
            SMBConfiguration(
                id = 0,
                name = "Configuration 1",
                server = "Server 1",
                sharedPath = "SharedPath 1",
                user = "",
                psw = "",
            ),
            SMBConfiguration(
                id = 1,
                name = "Configuration 2",
                server = "Server 2",
                sharedPath = "SharedPath 2",
                user = "",
                psw = "",
            )
        ),
        smbConfigurationItemIdToDelete = null
    )
    private val userInteractions: UserInteractions = mock()

    @Test
    fun testLoadedViewIsDisplayedCorrectly() {
        composeTestRule.setContent {
            MainLoadedView(loaded, userInteractions)
        }

        assertConfigurationsAreDisplayed(1)
        assertConfigurationsAreDisplayed(2)
        val contentDescription = "Add a new network configuration"
        composeTestRule.onNodeWithContentDescription(contentDescription).assertIsDisplayed()
    }

    private fun assertConfigurationsAreDisplayed(position: Int) {
        composeTestRule.onNodeWithText("Configuration $position").assertIsDisplayed()
        composeTestRule.onNodeWithText("Server: Server $position").assertIsDisplayed()
        composeTestRule.onNodeWithText("Path: SharedPath $position").assertIsDisplayed()
    }

    @Test
    fun testDeleteDialogIsDisplayedCorrectly() {
        composeTestRule.setContent {
            MainLoadedView(loaded.copy(smbConfigurationItemIdToDelete = 0), userInteractions)
        }

        val title = "Do you want to delete this configuration?"
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        val confirmButtonText = "Delete"
        composeTestRule.onNodeWithText(confirmButtonText).assertIsDisplayed()
        val cancelButtonText = "Cancel"
        composeTestRule.onNodeWithText(cancelButtonText).assertIsDisplayed()
    }
}