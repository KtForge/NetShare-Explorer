package com.msd.feature.explorer.presenter

import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.domain.smb.model.SMBConfiguration
import org.junit.Test

class ExplorerStateTest {

    @Test
    fun `when state is loading should return is initialized`() {
        val state = ExplorerState.Loading(name = "name", path = "path")

        val result = state.isUninitialized()

        assert(!result)
    }

    @Test
    fun `when state is loaded should return is initialized`() {
        val state = ExplorerState.Loaded(
            smbConfiguration = SMBConfiguration(
                id = null,
                name = "name",
                server = "server",
                sharedPath = "sharedPath",
                user = "user",
                psw = "psw"
            ),
            parentDirectory = null,
            workingDirectory = WorkingDirectory(path = "path", absolutePath = "absolutePath"),
            path = "path",
            filesOrDirectories = emptyList(),
            fileAccessError = null,
            isDownloadingFile = false,
        )

        val result = state.isUninitialized()

        assert(!result)
    }

    @Test
    fun `when state is error should return is initialized`() {
        val state = ExplorerState.Error.AccessError(name = "name", path = "path")

        val result = state.isUninitialized()

        assert(!result)
    }
}