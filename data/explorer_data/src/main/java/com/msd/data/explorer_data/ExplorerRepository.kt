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
        absolutePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> =
        dataSource.getFilesAndDirectories(server, sharedPath, absolutePath, user, psw)
            .sortedBy { fileOrDirectory -> fileOrDirectory.name }
            .sortedBy { fileOrDirectory -> fileOrDirectory is NetworkFile }

    override suspend fun openFile(
        server: String,
        sharedPath: String,
        absolutePath: String,
        fileName: String,
        user: String,
        psw: String,
        progressListener: (Float) -> Unit
    ) = dataSource.openFile(server, sharedPath, absolutePath, fileName, user, psw, progressListener)

    override fun deleteFile(filePath: String) = dataSource.deleteLocalFile(filePath)
}
