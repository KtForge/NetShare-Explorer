package com.msd.feature.explorer.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.feature.explorer.presenter.UserInteractions
import com.msd.feature.explorer.ui.ExplorerLoadedView

@Composable
@Preview
fun ExplorerLoadedPreview() {
    val loaded = ExplorerState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "Shared path",
            user = "User",
            psw = "Password"
        ),
        parentDirectory = null,
        workingDirectory = WorkingDirectory("", ""),
        path = "",
        filesOrDirectories = listOf(
            NetworkDirectory("directory 1", "", ""),
            NetworkDirectory("directory 2", "", ""),
            NetworkFile("file 1", "", "", false),
            NetworkFile("file 2", "", "", false),
            NetworkFile("file 3", "", "", true),
        ),
        fileAccessError = null,
        isDownloadingFile = false,
    )
    val userInteractions = object : UserInteractions {
        override fun onItemClicked(file: IBaseFile) = Unit
        override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) = Unit
        override fun onBackPressed() = Unit
        override fun onNavigateUp() = Unit
        override fun confirmFileAccessErrorDialog() = Unit
        override fun dismissFileAccessErrorDialog() = Unit
        override fun dismissProgressDialog() = Unit
        override fun downloadFile(file: NetworkFile) = Unit
        override fun deleteFile(file: NetworkFile) = Unit

    }
    NetworkStorageConfigurationTheme {
        ExplorerLoadedView(loaded, userInteractions)
    }
}

@Composable
@Preview
fun ExplorerLoadedParentDirectoryPreview() {
    val loaded = ExplorerState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "Shared path",
            user = "User",
            psw = "Password"
        ),
        parentDirectory = ParentDirectory("..", "", ""),
        workingDirectory = WorkingDirectory("", ""),
        path = "",
        filesOrDirectories = listOf(
            NetworkDirectory("directory 1", "", ""),
            NetworkDirectory("directory 2", "", ""),
            NetworkFile("file 1", "", "", false),
            NetworkFile("file 2", "", "", false),
            NetworkFile("file 3", "", "", true),
        ),
        fileAccessError = null,
        isDownloadingFile = false,
    )
    val userInteractions = object : UserInteractions {
        override fun onItemClicked(file: IBaseFile) = Unit
        override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) = Unit
        override fun onBackPressed() = Unit
        override fun onNavigateUp() = Unit
        override fun confirmFileAccessErrorDialog() = Unit
        override fun dismissFileAccessErrorDialog() = Unit
        override fun dismissProgressDialog() = Unit
        override fun downloadFile(file: NetworkFile) = Unit
        override fun deleteFile(file: NetworkFile) = Unit

    }
    NetworkStorageConfigurationTheme {
        ExplorerLoadedView(loaded, userInteractions)
    }
}

@Composable
@Preview
fun ExplorerLoadedErrorDialogPreview() {
    val loaded = ExplorerState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "Shared path",
            user = "User",
            psw = "Password"
        ),
        parentDirectory = null,
        workingDirectory = WorkingDirectory("", ""),
        path = "",
        filesOrDirectories = listOf(
            NetworkDirectory("directory 1", "", ""),
            NetworkDirectory("directory 2", "", ""),
            NetworkFile("file 1", "", "", false),
            NetworkFile("file 2", "", "", false),
            NetworkFile("file 3", "", "", true),
        ),
        fileAccessError = ExplorerState.Error.AccessError("Name", "Public"),
        isDownloadingFile = false,
    )
    val userInteractions = object : UserInteractions {
        override fun onItemClicked(file: IBaseFile) = Unit
        override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) = Unit
        override fun onBackPressed() = Unit
        override fun onNavigateUp() = Unit
        override fun confirmFileAccessErrorDialog() = Unit
        override fun dismissFileAccessErrorDialog() = Unit
        override fun dismissProgressDialog() = Unit
        override fun downloadFile(file: NetworkFile) = Unit
        override fun deleteFile(file: NetworkFile) = Unit

    }
    NetworkStorageConfigurationTheme {
        ExplorerLoadedView(loaded, userInteractions)
    }
}

@Composable
@Preview
fun ExplorerLoadedDownloadDialogPreview() {
    val loaded = ExplorerState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "Shared path",
            user = "User",
            psw = "Password"
        ),
        parentDirectory = null,
        workingDirectory = WorkingDirectory("", ""),
        path = "",
        filesOrDirectories = listOf(
            NetworkDirectory("directory 1", "", ""),
            NetworkDirectory("directory 2", "", ""),
            NetworkFile("file 1", "", "", false),
            NetworkFile("file 2", "", "", false),
            NetworkFile("file 3", "", "", true),
        ),
        fileAccessError = null,
        isDownloadingFile = true,
    )
    val userInteractions = object : UserInteractions {
        override fun onItemClicked(file: IBaseFile) = Unit
        override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) = Unit
        override fun onBackPressed() = Unit
        override fun onNavigateUp() = Unit
        override fun confirmFileAccessErrorDialog() = Unit
        override fun dismissFileAccessErrorDialog() = Unit
        override fun dismissProgressDialog() = Unit
        override fun downloadFile(file: NetworkFile) = Unit
        override fun deleteFile(file: NetworkFile) = Unit

    }
    NetworkStorageConfigurationTheme {
        ExplorerLoadedView(loaded, userInteractions)
    }
}
