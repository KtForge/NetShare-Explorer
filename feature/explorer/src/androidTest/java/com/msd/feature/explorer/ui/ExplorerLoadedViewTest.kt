package com.msd.feature.explorer.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.UserInteractions
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

class ExplorerLoadedViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val parentDirectoryName = ".."
    private val directoryName = "Directory"
    private val file1Name = "File 1"
    private val file2Name = "File 2"

    private val parentDirectory = ParentDirectory(
        name = "..",
        path = "",
        absolutePath = "\\\\Server\\SharedPath"
    )
    private val directory = NetworkDirectory(
        name = directoryName,
        path = directoryName,
        absolutePath = "\\\\Server\\SharedPath\\Path\\Directory"
    )
    private val file1 = NetworkFile(
        name = file1Name,
        path = file1Name,
        localPath = "",
        isLocal = false,
    )
    private val file2 = NetworkFile(
        name = file2Name,
        path = file2Name,
        localPath = "",
        isLocal = true,
    )

    private val loaded = Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Configuration",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw"
        ),
        parentDirectory = null,
        workingDirectory = WorkingDirectory("Path", "\\\\Server\\SharedPath\\Path"),
        path = "Path",
        filesOrDirectories = listOf(directory, file1, file2),
        fileAccessError = null,
        isDownloadingFile = false,
    )
    private val userInteractions: UserInteractions = mock()

    @Test
    fun testLoadedViewRootDirectoryIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText(parentDirectoryName).assertDoesNotExist()
            onNodeWithText(directoryName).assertIsDisplayed()
            onNodeWithText(file1Name).assertIsDisplayed()
            onNodeWithText(file2Name).assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testLoadedViewNonRootDirectoryIsDisplayedCorrectly() {
        val loaded = loaded.copy(parentDirectory = parentDirectory)

        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        with(composeTestRule) {
            onNodeWithText(parentDirectoryName).assertIsDisplayed()
            onNodeWithText(directoryName).assertIsDisplayed()
            onNodeWithText(file1Name).assertIsDisplayed()
            onNodeWithText(file2Name).assertIsDisplayed()
        }
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testLoadedViewFileConnectionErrorIsDisplayedCorrectly() {
        val loaded = loaded.copy(fileAccessError = Error.ConnectionError("Configuration", "Path"))

        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        val errorText =
            "Shared folder can't be accessed. Something happened when connecting to the server."
        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testLoadedViewFileAccessErrorIsDisplayedCorrectly() {
        val loaded = loaded.copy(fileAccessError = Error.AccessError("Configuration", "Path"))

        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        val errorText = "Access denied. Please check your credentials."
        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testLoadedViewUnknownErrorIsDisplayedCorrectly() {
        val loaded = loaded.copy(fileAccessError = Error.UnknownError("Configuration", "Path"))

        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        val errorText =
            "Shared folder can't be accessed. Something happened when connecting to the server."
        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testLoadedViewDownloadProgressIsDisplayedCorrectly() {
        val loaded = loaded.copy(isDownloadingFile = true)

        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Downloading file").assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testClickOnParentDirectoryWorksAsExpected() {
        val loaded = loaded.copy(parentDirectory = parentDirectory)
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText(parentDirectoryName).performClick()

        verify(userInteractions).onParentDirectoryClicked(parentDirectory)
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnDirectoryWorksAsExpected() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText(directoryName).performClick()

        verify(userInteractions).onItemClicked(directory)
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnFileWorksAsExpected() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText(file1Name).performClick()

        verify(userInteractions).onItemClicked(file1)
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnEditFileAccessErrorWorksAsExpected() {
        val loaded = loaded.copy(fileAccessError = Error.ConnectionError("Configuration", "Path"))
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Edit").performClick()

        verify(userInteractions).confirmFileAccessErrorDialog()
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnDismissFileAccessErrorWorksAsExpected() {
        val loaded = loaded.copy(fileAccessError = Error.ConnectionError("Configuration", "Path"))
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Accept").performClick()

        verify(userInteractions).dismissFileAccessErrorDialog()
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnDismissDownloadProgressWorksAsExpected() {
        val loaded = loaded.copy(isDownloadingFile = true)
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onNodeWithText("Cancel").performClick()

        verify(userInteractions).dismissProgressDialog()
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testNonDownloadedFileMenuIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onAllNodesWithContentDescription("More options")[0].performClick()

        composeTestRule.onNodeWithText("Download").assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testDownloadedFileMenuIsDisplayedCorrectly() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onAllNodesWithContentDescription("More options")[1].performClick()

        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        verifyNoInteractions(userInteractions)
    }

    @Test
    fun testClickOnDownloadFileWorksAsExpected() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onAllNodesWithContentDescription("More options")[0].performClick()
        composeTestRule.onNodeWithText("Download").performClick()

        verify(userInteractions).downloadFile(file1)
        verifyNoMoreInteractions(userInteractions)
    }

    @Test
    fun testClickOnDeleteFileWorksAsExpected() {
        composeTestRule.setContent { ExplorerLoadedView(loaded, userInteractions) }

        composeTestRule.onAllNodesWithContentDescription("More options")[1].performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        verify(userInteractions).deleteFile(file2)
        verifyNoMoreInteractions(userInteractions)
    }
}
