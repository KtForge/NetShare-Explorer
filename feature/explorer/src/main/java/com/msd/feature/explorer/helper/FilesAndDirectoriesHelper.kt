package com.msd.feature.explorer.helper

import com.msd.domain.explorer.DeleteFileUseCase
import com.msd.domain.explorer.DownloadFileUseCase
import com.msd.domain.explorer.GetFilesAndDirectoriesUseCase
import com.msd.domain.explorer.OpenFileUseCase
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.smb.model.SMBConfiguration
import java.io.File
import javax.inject.Inject

class FilesAndDirectoriesHelper @Inject constructor(
    private val getFilesAndDirectoriesUseCase: GetFilesAndDirectoriesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val openFileUseCase: OpenFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
) {

    fun getRootPath(smbConfiguration: SMBConfiguration): String {
        return "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"
    }

    @Throws(Exception::class)
    suspend fun getFilesAndDirectories(
        smbConfiguration: SMBConfiguration,
        path: String
    ): FilesResult {

        return getFilesAndDirectoriesUseCase(
            server = smbConfiguration.server,
            sharedPath = smbConfiguration.sharedPath,
            directoryPath = path,
            user = smbConfiguration.user,
            psw = smbConfiguration.psw
        )
    }

    @Throws(Exception::class)
    suspend fun downloadFile(
        smbConfiguration: SMBConfiguration,
        file: NetworkFile
    ) = downloadFileUseCase(
        server = smbConfiguration.server,
        sharedPath = smbConfiguration.sharedPath,
        fileName = file.name,
        filePath = file.path,
        localFilePath = file.localPath,
        user = smbConfiguration.user,
        psw = smbConfiguration.psw,
    )

    @Throws(Exception::class)
    suspend fun openFile(
        smbConfiguration: SMBConfiguration,
        file: NetworkFile,
    ): File {
        return openFileUseCase(
            server = smbConfiguration.server,
            sharedPath = smbConfiguration.sharedPath,
            fileName = file.name,
            filePath = file.path,
            localFilePath = file.localPath,
            user = smbConfiguration.user,
            psw = smbConfiguration.psw,
        )
    }

    fun deleteFile(file: NetworkFile) {
        deleteFileUseCase(file)
    }
}
