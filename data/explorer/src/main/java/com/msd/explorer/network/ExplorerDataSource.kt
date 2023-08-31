package com.msd.explorer.network

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mserref.NtStatus
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.msd.explorer.mapper.FilesAndDirectoriesMapper.toBaseFile
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.SMBException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.net.SocketTimeoutException
import java.util.EnumSet
import javax.inject.Inject

private const val ROOT_PATH = ""

class ExplorerDataSource @Inject constructor(@ApplicationContext private val appContext: Context) {

    fun getFilesAndDirectories(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> {
        val client = SMBClient()

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

                    directory.list().mapNotNull { file ->
                        file.toBaseFile(diskShare, parentPath = parentPath)
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    fun openFile(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        fileName: String,
        user: String,
        psw: String,
    ): File? {
        val client = SMBClient()

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

                    val folder = File(appContext.cacheDir, "$server/$sharedPath/$parentPath")
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    val newFile =
                        File(appContext.cacheDir, "$server/$sharedPath/$parentPath/$fileName")
                    file.inputStream.use { _is ->
                        FileOutputStream(newFile).use { output ->
                            _is.copyTo(output)
                        }
                    }
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
