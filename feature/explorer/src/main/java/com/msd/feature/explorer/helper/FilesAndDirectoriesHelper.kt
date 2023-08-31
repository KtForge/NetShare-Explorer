package com.msd.feature.explorer.helper

import com.msd.domain.explorer.GetFilesAndDirectoriesUseCase
import com.msd.domain.explorer.OpenFileUseCase
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.smb.model.SMBConfiguration
import java.io.File
import javax.inject.Inject

class FilesAndDirectoriesHelper @Inject constructor(
    private val getFilesAndDirectoriesUseCase: GetFilesAndDirectoriesUseCase,
    private val openFileUseCase: OpenFileUseCase,
) {

    fun getRootPath(smbConfiguration: SMBConfiguration): String {
        return "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"
    }

    @Throws(Exception::class)
    suspend fun getFilesAndDirectories(
        smbConfiguration: SMBConfiguration,
        path: String
    ): List<IBaseFile> {

        return getFilesAndDirectoriesUseCase(
            server = smbConfiguration.server,
            sharedPath = smbConfiguration.sharedPath,
            directoryRelativePath = path,
            user = smbConfiguration.user,
            psw = smbConfiguration.psw
        )
    }

    @Throws(Exception::class)
    suspend fun openFile(smbConfiguration: SMBConfiguration, file: IBaseFile, path: String): File? {
        return openFileUseCase(
            server = smbConfiguration.server,
            sharedPath = smbConfiguration.sharedPath,
            directoryRelativePath = path,
            fileName = file.name,
            user = smbConfiguration.user,
            psw = smbConfiguration.psw
        )
    }
}