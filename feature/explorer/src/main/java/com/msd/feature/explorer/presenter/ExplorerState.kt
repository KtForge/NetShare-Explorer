package com.msd.feature.explorer.presenter

import androidx.annotation.StringRes
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.explorer.R
import com.msd.presentation.State

sealed class ExplorerState(open val name: String, open val path: String) : State {

    data class Uninitialized(
        override val name: String,
        override val path: String
    ) : ExplorerState(name, path)

    data class Loading(
        override val name: String,
        override val path: String
    ) : ExplorerState(name, path)

    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        val parentDirectory: ParentDirectory?,
        val workingDirectory: WorkingDirectory,
        override val path: String,
        val filesOrDirectories: List<IBaseFile>,
        val fileAccessError: Error?,
        val isDownloadingFile: Boolean,
    ) : ExplorerState(smbConfiguration.name, path)

    sealed class Error(
        override val name: String,
        override val path: String,
        @StringRes val message: Int
    ) : ExplorerState(name, path) {
        data class ConnectionError(
            override val name: String,
            override val path: String
        ) : Error(name, path, R.string.connection_error_message)

        data class AccessError(
            override val name: String,
            override val path: String
        ) : Error(name, path, R.string.access_error_message)

        data class UnknownError(
            override val name: String,
            override val path: String
        ) : Error(name, path, R.string.connection_error_message)
    }
}
