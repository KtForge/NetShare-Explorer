package com.msd.domain.explorer

import com.msd.domain.explorer.model.IBaseFile
import java.io.File
import kotlin.jvm.Throws

interface IExplorerRepository {

    @Throws(Exception::class)
    suspend fun retrieveFilesAndDirectories(
        server: String,
        sharedPath: String,
        absolutePath: String,
        user: String,
        psw: String
    ): List<IBaseFile>

    @Throws(Exception::class)
    suspend fun openFile(
        server: String,
        sharedPath: String,
        absolutePath: String,
        fileName: String,
        user: String,
        psw: String,
    ): File

    fun deleteFile(filePath: String)
}
