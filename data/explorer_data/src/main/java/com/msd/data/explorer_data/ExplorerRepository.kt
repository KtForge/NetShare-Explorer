package com.msd.data.explorer_data

import com.msd.data.explorer_data.network.ExplorerDataSource
import com.msd.domain.explorer.IExplorerRepository
import com.msd.domain.explorer.model.FilesResult
import java.io.File
import javax.inject.Inject

class ExplorerRepository @Inject constructor(
    private val dataSource: ExplorerDataSource
) : IExplorerRepository {

    override suspend fun retrieveFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryPath: String,
        user: String,
        psw: String
    ): FilesResult = dataSource.getFilesResult(server, sharedPath, directoryPath, user, psw)

    override suspend fun downloadFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String
    ) = dataSource.downloadFile(server, sharedPath, fileName, filePath, localFilePath, user, psw)

    override suspend fun openFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    ): File {
        val isLocalFileValid = dataSource.isLocalFileValid(
            server,
            sharedPath,
            fileName,
            filePath,
            localFilePath,
            user,
            psw
        )

        if (!isLocalFileValid) {
            downloadFile(server, sharedPath, fileName, filePath, localFilePath, user, psw)
        }

        return dataSource.openFile(localFilePath, fileName)
    }

    override fun deleteFile(localFilePath: String, fileName: String) =
        dataSource.deleteLocalFile(localFilePath, fileName)
}
