package com.msd.data.explorer_data.network

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mserref.NtStatus
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import com.msd.data.explorer_data.mapper.IFilesAndDirectoriesMapper
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.SMBException
import kotlinx.coroutines.CancellationException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.EnumSet
import javax.inject.Inject

class SMBHelper @Inject constructor(
    private val client: SMBClient,
    private val filesAndDirectoriesMapper: IFilesAndDirectoriesMapper
) {

    @Throws(Exception::class)
    suspend fun <T : Any> onConnection(
        server: String,
        sharedPath: String,
        user: String,
        psw: String,
        block: suspend (DiskShare) -> T,
    ): T {
        return try {
            client.connect(server).use { connection ->
                val authenticationContext = AuthenticationContext(user, psw.toCharArray(), server)
                val session = connection.authenticate(authenticationContext)

                (session.connectShare(sharedPath) as? DiskShare)?.use { diskShare ->
                    block(diskShare)
                } ?: throw SMBException.UnknownError
            }
        } catch (e: Exception) {
            throw handleException(e)
        } finally {
            client.close()
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

    fun listFiles(
        server: String,
        sharedPath: String,
        path: String,
        diskShare: DiskShare,
        fileManager: FileManager,
    ): FilesResult {
        val directory = diskShare.openDirectory(
            path,
            EnumSet.of(AccessMask.FILE_READ_DATA),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )

        return filesAndDirectoriesMapper.buildFilesResult(
            server,
            sharedPath,
            path,
            directory.list(),
            fileManager
        )
    }

    fun getFileSize(diskShare: DiskShare, filePath: String, fileName: String): Long {
        val file = openFile(diskShare, filePath, fileName)

        return file.fileInformation.standardInformation.endOfFile
    }

    fun getModificationTime(diskShare: DiskShare, filePath: String, fileName: String): Long {
        val file = openFile(diskShare, filePath, fileName)

        return file.fileInformation.basicInformation.changeTime.toEpochMillis()
    }

    fun getInputStream(diskShare: DiskShare, filePath: String, fileName: String): InputStream {
        val file = openFile(diskShare, filePath, fileName)

        return file.inputStream
    }

    private fun openFile(diskShare: DiskShare, filePath: String, fileName: String): File {
        return diskShare.openFile(
            "$filePath/$fileName",
            EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
    }
}