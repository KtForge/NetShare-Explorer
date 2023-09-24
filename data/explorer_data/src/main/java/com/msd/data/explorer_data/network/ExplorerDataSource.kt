package com.msd.data.explorer_data.network

import com.hierynomus.mserref.NtStatus
import com.hierynomus.mssmb2.SMBApiException
import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.SMBException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import javax.inject.Inject
import com.hierynomus.smbj.share.File as SMBFile

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
            throw handleException(exception)
        }
    }

    @Throws(Exception::class)
    suspend fun downloadFile(
        server: String,
        sharedPath: String,
        filePath: String,
        fileName: String,
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
                val remoteFile = smbHelper.openFile(diskShare, filePath, fileName)

                val localFile =
                    fileManager.getLocalFileRef(server, sharedPath, filePath, fileName)
                val fileSize = smbHelper.getFileSize(remoteFile)

                remoteFile.inputStream.copyTo(localFile.outputStream())

                val openTime = System.currentTimeMillis() - start
                // TODO Tracking
                // explorerTracker.logOpenLocalFileEvent(fileSize, openTime)
            }
        } catch (exception: Exception) {
            // Delete local file
            val localFile = fileManager.getLocalFileRef(server, sharedPath, filePath, fileName)
            if (localFile.exists()) {
                localFile.delete()
            }

            throw handleException(exception)
        }
    }

    @Throws(Exception::class)
    suspend fun openFile(
        server: String,
        sharedPath: String,
        filePath: String,
        fileName: String,
        user: String,
        psw: String,
    ): File {
        val start = System.currentTimeMillis()

        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                val remoteFile = smbHelper.openFile(diskShare, filePath, fileName)

                val localFile =
                    fileManager.getLocalFileRef(server, sharedPath, filePath, fileName)
                val fileSize = smbHelper.getFileSize(remoteFile)

                if (!isLocalFileValid(localFile, remoteFile)) {
                    remoteFile.inputStream.copyTo(localFile.outputStream())
                }

                val openTime = System.currentTimeMillis() - start
                explorerTracker.logOpenLocalFileEvent(fileSize, openTime)

                localFile
            }
        } catch (exception: Exception) {
            // Delete local file
            val localFile = fileManager.getLocalFileRef(server, sharedPath, filePath, fileName)
            if (localFile.exists()) {
                localFile.delete()
            }

            throw handleException(exception)
        }
    }

    fun deleteLocalFile(filePath: String) {
        fileManager.deleteFile(filePath)
    }

    private fun isLocalFileValid(localFile: File, remoteFile: SMBFile): Boolean {
        if (localFile.exists()) {
            val fileLastChangeTime = smbHelper.getModificationTime(remoteFile)
            val localFileCreationTime = fileManager.getCreationTimeMillis(localFile)

            if (localFileCreationTime > fileLastChangeTime) {
                return true
            }
        }

        return false
    }

    private suspend fun InputStream.copyTo(out: OutputStream) {
        var bytesCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buffer)

        while (bytes >= 0) {
            yield()
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            // progressListener(bytesCopied.toFloat().div(fileSize))
            bytes = read(buffer)
        }
    }

    private fun handleException(exception: Exception): Throwable {
        return when (exception) {
            is SocketTimeoutException -> SMBException.ConnectionError
            is SMBApiException -> {
                when (exception.status) {
                    NtStatus.STATUS_ACCESS_DENIED -> SMBException.AccessDenied
                    else -> SMBException.UnknownError
                }
            }

            is CancellationException -> SMBException.CancelException
            else -> SMBException.UnknownError
        }
    }
}
