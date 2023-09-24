package com.msd.domain.explorer

import com.msd.domain.explorer.model.FilesResult
import java.io.File

interface IExplorerRepository {

    @Throws(Exception::class)
    suspend fun retrieveFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryPath: String,
        user: String,
        psw: String
    ): FilesResult

    @Throws(Exception::class)
    suspend fun downloadFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    )

    @Throws(Exception::class)
    suspend fun openFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    ): File

    fun deleteFile(localFilePath: String, fileName: String)
}
