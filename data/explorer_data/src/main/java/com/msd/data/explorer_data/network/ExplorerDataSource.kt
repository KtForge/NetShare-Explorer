package com.msd.data.explorer_data.network

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mserref.NtStatus
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
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
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.EnumSet
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream
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

                    cleanFiles(server, sharedPath, parentPath, filesAndDirectories)

                    filesAndDirectories
                } ?: emptyList()
            }
        } catch (e: Exception) {
            throw handleException(e)
        } finally {
            client.close()
        }
    }

    private fun cleanFiles(
        server: String,
        sharedPath: String,
        path: String,
        filesAndDirectories: List<IBaseFile>
    ) {
        val cacheDir = appContext.cacheDir
        val cacheServerPath = "${cacheDir.absolutePath}/$server/$sharedPath/$path"
        val cacheServerDirectory = File(cacheServerPath)

        if (cacheServerDirectory.exists()) {
            cacheServerDirectory.listFiles()?.filter { it.isFile }?.forEach { file ->
                if (filesAndDirectories.all { file.name != it.name }) {
                    file.delete()
                }
            }
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

        try {
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

                    val localFile = File(appContext.cacheDir, "$path/$fileName")
                    val fileSize =
                        file.getFileInformation(FileStandardInformation::class.java).endOfFile

                    if (localFile.exists() && localFile.length() == fileSize) {
                        return localFile
                    }

                    file.inputStream.copyTo(localFile.outputStream())

                    val openTime = System.currentTimeMillis() - start

                    explorerTracker.logOpenFileEvent(fileSize, openTime)

                    return localFile
                } ?: return null
            }
        } catch (e: Exception) {
            throw handleException(e)
        } finally {
            client.close()
        }
    }

    private fun getChecksumCRC32(stream: InputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
        val checkedInputStream = CheckedInputStream(stream, CRC32())
        val buffer = ByteArray(bufferSize)
        while (checkedInputStream.read(buffer, 0, buffer.size) >= 0) {
            // Loop
        }

        return checkedInputStream.checksum.value
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
