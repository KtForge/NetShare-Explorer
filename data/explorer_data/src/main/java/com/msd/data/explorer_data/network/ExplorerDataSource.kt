package com.msd.data.explorer_data.network

import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import java.io.File
import javax.inject.Inject

class ExplorerDataSource @Inject constructor(
    private val smbHelper: SMBHelper,
    private val fileManager: FileManager,
    private val explorerTracker: ExplorerTracker,
) {

    @Throws(Exception::class)
    suspend fun getFilesResult(
        server: String,
        sharedPath: String,
        directoryPath: String,
        user: String,
        psw: String
    ): FilesResult {
        val start = System.currentTimeMillis()

        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                val filesResult =
                    smbHelper.listFiles(server, sharedPath, directoryPath, diskShare, fileManager)

                fileManager.cleanFiles(
                    server,
                    sharedPath,
                    directoryPath,
                    filesResult.filesAndDirectories
                )

                val openTime = System.currentTimeMillis() - start
                explorerTracker.logListFilesAndDirectoriesEvent(
                    filesResult.filesAndDirectories.size,
                    openTime
                )

                filesResult
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    @Throws(Exception::class)
    suspend fun downloadFile(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    ) {
        val start = System.currentTimeMillis()

        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                fileManager.makeDirectoriesForNewFile(server, sharedPath, filePath)
                val localFile = fileManager.getLocalFile(localFilePath, fileName)

                val fileSize = smbHelper.getFileSize(diskShare, filePath, fileName)

                fileManager.copyFile(
                    inputStream = smbHelper.getInputStream(diskShare, filePath, fileName),
                    localFile
                )

                val openTime = System.currentTimeMillis() - start
                explorerTracker.logDownloadFile(fileSize, openTime)
            }
        } catch (exception: Exception) {
            // Delete local file
            val localFile = fileManager.getLocalFile(localFilePath, fileName)
            if (localFile.exists()) {
                localFile.delete()
            }

            throw exception
        }
    }

    @Throws(Exception::class)
    suspend fun isLocalFileValid(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    ): Boolean {
        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                val localFile = fileManager.getLocalFile(localFilePath, fileName)

                if (localFile.exists()) {
                    val fileLastChangeTime =
                        smbHelper.getModificationTime(diskShare, filePath, fileName)
                    val localFileCreationTime = fileManager.getCreationTimeMillis(localFile)

                    if (localFileCreationTime > fileLastChangeTime) {
                        return@onConnection true
                    }
                }

                return@onConnection false
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    @Throws(Exception::class)
    fun openFile(localFilePath: String, fileName: String): File {
        return fileManager.getLocalFile(localFilePath, fileName).also {
            explorerTracker.logOpenFileEvent(it.length())
        }
    }

    fun deleteLocalFile(localFilePath: String, fileName: String) {
        fileManager.deleteFile(localFilePath, fileName)
    }
}
