package com.msd.data.explorer_data

import com.msd.data.explorer_data.network.ExplorerDataSource
import com.msd.domain.explorer.IExplorerRepository
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkFile
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
    ): List<IBaseFile> =
        dataSource.getFilesAndDirectories(server, sharedPath, directoryPath, user, psw)
            .sortedBy { fileOrDirectory -> fileOrDirectory.name }
            .sortedBy { fileOrDirectory -> fileOrDirectory is NetworkFile }

    override suspend fun downloadFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        user: String,
        psw: String
    ) = dataSource.downloadFile(server, sharedPath, filePath, fileName, user, psw)

    override suspend fun openFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        user: String,
        psw: String,
    ) = dataSource.openFile(server, sharedPath, filePath, fileName, user, psw)

    override fun deleteFile(filePath: String) = dataSource.deleteLocalFile(filePath)
}
