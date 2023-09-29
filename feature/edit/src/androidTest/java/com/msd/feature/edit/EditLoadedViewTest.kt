package com.msd.feature.edit

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.edit.presenter.EditState.Loaded
import com.msd.feature.edit.presenter.UserInteractions
import com.msd.feature.edit.ui.EditLoadedView
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

class EditLoadedViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loaded = Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Configuration",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        ),
        isPasswordVisible = false,
        actionButtonLabel = R.string.edit_configuration_button,
        serverError = false,
        sharedPathError = false,
    )
    private val userInteractions: UserInteractions = mock()

    @Test
    fun editConfigurationIsDisplayedCorrectly() {
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText("Configuration").assertIsDisplayed()
            onNodeWithText("Server").assertIsDisplayed()
            onNodeWithText("SharedPath").assertIsDisplayed()
            onNodeWithText("User").assertIsDisplayed()
            onNodeWithText("Psw").assertDoesNotExist()
            onNodeWithText("•••").assertIsDisplayed()
            onNodeWithText("Edit").assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun editConfigurationAndPasswordVisibleIsDisplayedCorrectly() {
        val loaded = loaded.copy(isPasswordVisible = true)
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText("Configuration").assertIsDisplayed()
            onNodeWithText("Server").assertIsDisplayed()
            onNodeWithText("SharedPath").assertIsDisplayed()
            onNodeWithText("User").assertIsDisplayed()
            onNodeWithText("Psw").assertIsDisplayed()
            onNodeWithText("•••").assertDoesNotExist()
            onNodeWithText("Edit").assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun newConfigurationIsDisplayedCorrectly() {
        val loaded = loaded.copy(
            smbConfiguration = SMBConfiguration(
                id = null,
                name = "",
                server = "",
                sharedPath = "",
                user = "",
                psw = ""
            ),
            actionButtonLabel = R.string.save_configuration_button,
        )
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText("Name (optional)", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText("Server", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText("Shared path", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText("User (optional)", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText("Password (optional)", useUnmergedTree = true).assertIsDisplayed()
            onAllNodesWithText("").assertCountEquals(5)
            onNodeWithText("Save").assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun errorsAreDisplayedCorrectly() {
        val loaded = loaded.copy(
            serverError = true,
            sharedPathError = true,
        )
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText("Server can't be empty", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText("Shared path can't be empty", useUnmergedTree = true).assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun clickOnEditConfigurationWorksAsExpected() {
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Edit").performClick()

        verify(userInteractions).onConfirmButtonClicked(
            name = "Configuration",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun clickOnSaveConfigurationWorksAsExpected() {
        val loaded = loaded.copy(
            smbConfiguration = SMBConfiguration(
                id = null,
                name = "",
                server = "",
                sharedPath = "",
                user = "",
                psw = ""
            ),
            actionButtonLabel = R.string.save_configuration_button,
        )
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Save").performClick()

        verify(userInteractions).onConfirmButtonClicked(
            name = "",
            server = "",
            sharedPath = "",
            user = "",
            psw = "",
        )
        verifyNoMoreInteractions(userInteractions)
    }

    /*@Test
    fun clickOnSaveConfigurationChangesWorksAsExpected() {
        val loaded = loaded.copy(
            smbConfiguration = SMBConfiguration(
                id = null,
                name = "",
                server = "",
                sharedPath = "",
                user = "",
                psw = ""
            ),
            actionButtonLabel = R.string.save_configuration_button,
        )
        composeTestRule.setContent { EditLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText("Name (optional)", useUnmergedTree = true)
                .onParent()
                .performTextInput("Name")
            onNodeWithText("Server", useUnmergedTree = true)
                .onParent()
                .performTextInput("Server 1")
            onNodeWithText("Shared path", useUnmergedTree = true)
                .onParent()
                .performTextInput("Shared")
            onNodeWithText("User (optional)", useUnmergedTree = true)
                .onParent()
                .performTextInput("User")
            onNodeWithText("Password (optional)", useUnmergedTree = true)
                .onParent()
                .performTextInput("1234")
            onNodeWithText("Save").performClick()
        }

        verify(userInteractions).onConfirmButtonClicked(
            name = "Name",
            server = "Server 1",
            sharedPath = "Shared",
            user = "User",
            psw = "1234",
        )
        verifyNoMoreInteractions(userInteractions)
    }*/
}
