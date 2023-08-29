package com.msd.explorer.presenter

import com.msd.explorer.model.IBaseFile
import com.msd.presentation.State
import com.msd.smb.model.SMBConfiguration

sealed class ExplorerState(open val name: String) : State {

    data class Uninitialized(override val name: String) : ExplorerState(name)
    data class Loading(override val name: String) : ExplorerState(name)
    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        val root: String,
        val path: String,
        val filesOrDirectories: List<IBaseFile>
    ) : ExplorerState(smbConfiguration.name)

    sealed class Error(override val name: String) : ExplorerState(name) {
        data class ConnectionError(override val name: String) : Error(name)
        data class UnknownError(override val name: String) : Error(name)
    }

    override fun isUninitialized(): Boolean = this != Uninitialized("")
    override fun initialState(): State = Uninitialized("")
}
