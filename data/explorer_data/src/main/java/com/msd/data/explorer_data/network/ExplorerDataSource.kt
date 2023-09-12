package com.msd.data.explorer_data.network

import com.hierynomus.mserref.NtStatus
import com.hierynomus.mssmb2.SMBApiException
import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.SMBException
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
    fun getFilesAndDirectories(
        server: String,
        sharedPath: String,
        absolutePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> {
        val start = System.currentTimeMillis()

        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                val relativePath = smbHelper.getRelativePath(diskShare, absolutePath)
                val files = smbHelper.listFiles(diskShare, relativePath)
                fileManager.cleanFiles(server, sharedPath, relativePath, files)

                val openTime = System.currentTimeMillis() - start
                explorerTracker.logListFilesAndDirectoriesEvent(files.size, openTime)

                files
            }
        } catch (exception: Exception) {
            throw handleException(exception)
        }
    }

    @Throws(Exception::class)
    fun openFile(
        server: String,
        sharedPath: String,
        absolutePath: String,
        fileName: String,
        user: String,
        psw: String,
        progressListener: (Float) -> Unit,
    ): File {
        val start = System.currentTimeMillis()

        return try {
            smbHelper.onConnection(
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            ) { diskShare ->
                val relativePath = smbHelper.getRelativePath(diskShare, absolutePath)

                val remoteFile = smbHelper.openFile(diskShare, relativePath, fileName)

                val localFile =
                    fileManager.getLocalFileRef(server, sharedPath, relativePath, fileName)
                val fileSize = smbHelper.getFileSize(remoteFile)

                if (!isLocalFileValid(localFile, remoteFile)) {
                    remoteFile.inputStream.copyTo(
                        localFile.outputStream(),
                        fileSize,
                        progressListener
                    )
                }

                val openTime = System.currentTimeMillis() - start
                explorerTracker.logOpenLocalFileEvent(fileSize, openTime)

                localFile
            }
        } catch (exception: Exception) {
            throw handleException(exception)
        }
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

    private fun InputStream.copyTo(
        out: OutputStream,
        fileSize: Long,
        progressListener: (progress: Float) -> Unit
    ) {
        var bytesCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buffer)

        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            progressListener(bytesCopied.toFloat().div(fileSize))
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

            else -> SMBException.UnknownError
        }
    }
}
