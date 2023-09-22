package com.msd.data.explorer_data.mapper

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.DiskShare
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.NetworkParentDirectory
import java.util.EnumSet

private const val DOT_DIRECTORY = "."
private const val DOUBLE_DOT_DIRECTORY = ".."
private const val SEPARATOR = "\\"

object FilesAndDirectoriesMapper {

    // Generate all structure of files & directories
    fun FileIdBothDirectoryInformation.toBaseFile(
        server: String,
        sharedPath: String,
        diskShare: DiskShare,
        parentPath: String,
        fileManager: FileManager,
    ): IBaseFile? {
        if ((fileName == DOT_DIRECTORY && parentPath.isEmpty()) || fileName == DOUBLE_DOT_DIRECTORY) return null

        return if (fileAttributes == FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) {
            val directory = diskShare.openDirectory(
                parentPath + SEPARATOR + fileName,
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )

            if (fileName == DOT_DIRECTORY) {
                NetworkParentDirectory(fileName, directory.uncPath)
            } else {
                NetworkDirectory(fileName, directory.uncPath)
            }
        } else {
            val file = diskShare.openFile(
                parentPath + SEPARATOR + fileName,
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )

            val localPath = fileManager.getLocalFilePath(server, sharedPath, parentPath, fileName)
            val localFile = fileManager.getLocalFileRef(server, sharedPath, parentPath, fileName)
            val isLocal = localFile.exists()

            NetworkFile(fileName, localPath, isLocal)
        }
    }
}
