package com.msd.data.explorer_data.network

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mserref.NtStatus
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.msd.data.explorer_data.mapper.FilesAndDirectoriesMapper.toBaseFile
import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.SMBException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.net.SocketTimeoutException
import java.util.EnumSet
import javax.inject.Inject

private const val ROOT_PATH = ""

class ExplorerDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val client: SMBClient,
    private val explorerTracker: ExplorerTracker,
) {

    @Throws(Exception::class)
    fun getFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> {
        val start = System.currentTimeMillis()

        return try {
            client.connect(server).use { connection ->
                val authenticationContext = AuthenticationContext(user, psw.toCharArray(), server)
                val session = connection.authenticate(authenticationContext)

                (session.connectShare(sharedPath) as? DiskShare)?.use { diskShare ->
                    val parentPath = directoryRelativePath.replace(
                        diskShare.smbPath.toUncPath(),
                        ROOT_PATH
                    )
                    val directory = diskShare.openDirectory(
                        parentPath,
                        EnumSet.of(AccessMask.FILE_READ_DATA),
                        null,
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN,
                        null
                    )

                    val filesAndDirectories = directory.list().mapNotNull { file ->
                        file.toBaseFile(diskShare, parentPath = parentPath)
                    }

                    val openTime = System.currentTimeMillis() - start
                    val filesNumber = filesAndDirectories.size

                    explorerTracker.logListFilesAndDirectoriesEvent(filesNumber, openTime)

                    filesAndDirectories
                } ?: emptyList()
            }
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    @Throws(Exception::class)
    fun openFile(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        fileName: String,
        user: String,
        psw: String,
    ): File? {
        val start = System.currentTimeMillis()

        return try {
            client.connect(server).use { connection ->
                val authenticationContext = AuthenticationContext(user, psw.toCharArray(), server)
                val session = connection.authenticate(authenticationContext)

                (session.connectShare(sharedPath) as? DiskShare)?.use { diskShare ->
                    val parentPath = directoryRelativePath.replace(
                        diskShare.smbPath.toUncPath(),
                        ROOT_PATH
                    )

                    val file = diskShare.openFile(
                        parentPath + "\\" + fileName,
                        EnumSet.of(AccessMask.FILE_READ_DATA),
                        null,
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN,
                        null
                    )

                    val path = "$server/$sharedPath/$parentPath"
                    val folder = File(appContext.cacheDir, path)
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }

                    val newFile = File(appContext.cacheDir, "$path/$fileName")

                    file.inputStream.copyTo(newFile.outputStream())

                    val openTime = System.currentTimeMillis() - start
                    val fileSize = file.fileInformation.standardInformation.endOfFile

                    explorerTracker.logOpenFileEvent(fileSize, openTime)

                    newFile
                }
            }
        } catch (e: Exception) {
            throw handleException(e)
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
