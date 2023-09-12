package com.msd.feature.explorer.presenter

import androidx.annotation.StringRes
import com.msd.feature.explorer.R
import com.msd.domain.explorer.model.IBaseFile
import com.msd.presentation.State
import com.msd.domain.smb.model.SMBConfiguration

sealed class ExplorerState(open val name: String) : State {

    data class Uninitialized(override val name: String) : ExplorerState(name)
    data class Loading(override val name: String) : ExplorerState(name)
    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        val root: String,
        val path: String,
        val filesOrDirectories: List<IBaseFile>,
        val fileAccessError: Error?,
        val fileDownloadProgress: Float? = null,
    ) : ExplorerState(smbConfiguration.name)

    sealed class Error(override val name: String, @StringRes val message: Int) :
        ExplorerState(name) {
        data class ConnectionError(override val name: String) :
            Error(name, R.string.connection_error_message)

        data class AccessError(override val name: String) :
            Error(name, R.string.access_error_message)

        data class UnknownError(override val name: String) :
            Error(name, R.string.connection_error_message)
    }

    override fun isUninitialized(): Boolean = this != Uninitialized("")
    override fun initialState(): State = Uninitialized("")
}
