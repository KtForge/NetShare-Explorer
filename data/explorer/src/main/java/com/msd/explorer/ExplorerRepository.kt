package com.msd.explorer

import com.msd.explorer.model.IBaseFile
import com.msd.explorer.model.NetworkFile
import com.msd.explorer.network.ExplorerDataSource
import java.io.File
import javax.inject.Inject

class ExplorerRepository @Inject constructor(
    private val dataSource: ExplorerDataSource
) : IExplorerRepository {

    override suspend fun retrieveFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> =
        dataSource.getFilesAndDirectories(server, sharedPath, directoryRelativePath, user, psw)
            .sortedBy { fileOrDirectory -> fileOrDirectory.name }
            .sortedBy { fileOrDirectory -> fileOrDirectory is NetworkFile }

    override suspend fun openFile(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        fileName: String,
        user: String,
        psw: String
    ): File? = dataSource.openFile(server, sharedPath, directoryRelativePath, fileName, user, psw)
}