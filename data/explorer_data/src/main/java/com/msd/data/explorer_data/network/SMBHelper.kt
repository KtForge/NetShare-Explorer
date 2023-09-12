package com.msd.data.explorer_data.network

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import com.msd.data.explorer_data.mapper.FilesAndDirectoriesMapper.toBaseFile
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.SMBException
import java.util.EnumSet
import javax.inject.Inject

private const val ROOT_PATH = ""

class SMBHelper @Inject constructor(private val client: SMBClient) {

    fun <T : Any> onConnection(
        server: String,
        sharedPath: String,
        user: String,
        psw: String,
        block: (DiskShare) -> T,
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
            throw e
        } finally {
            client.close()
        }
    }

    fun getRelativePath(diskShare: DiskShare, absolutePath: String): String {
        return absolutePath.replace(diskShare.smbPath.toUncPath(), ROOT_PATH)
    }

    fun listFiles(diskShare: DiskShare, path: String): List<IBaseFile> {
        val directory = diskShare.openDirectory(
            path,
            EnumSet.of(AccessMask.FILE_READ_DATA),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )

        return directory.list().mapNotNull { file ->
            file.toBaseFile(diskShare, parentPath = path)
        }
    }

    fun openFile(diskShare: DiskShare, path: String, fileName: String): File {
        return diskShare.openFile(
            "$path/$fileName",
            EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
    }

    fun getFileSize(file: File): Long = file.fileInformation.standardInformation.endOfFile

    fun getModificationTime(file: File): Long {
        return file.fileInformation.basicInformation.changeTime.toEpochMillis()
    }
}