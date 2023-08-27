package com.msd.explorer

import com.msd.explorer.model.IBaseFile
import java.io.File

interface IExplorerRepository {

    suspend fun retrieveFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        user: String,
        psw: String
    ): List<IBaseFile>

    suspend fun openFile(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        fileName: String,
        user: String,
        psw: String
    ): File?
}
