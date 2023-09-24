package com.msd.data.explorer_data.mapper

import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile

private const val DOT_DIRECTORY = "."
private const val DOUBLE_DOT_DIRECTORY = ".."
private const val SEPARATOR = "\\"

object FilesAndDirectoriesMapper {

    // Generate all structure of files & directories
    fun FileIdBothDirectoryInformation.toBaseFile(
        server: String,
        sharedPath: String,
        parentPath: String,
        fileManager: FileManager,
    ): IBaseFile? {
        if ((fileName == DOT_DIRECTORY && parentPath.isEmpty()) || fileName == DOUBLE_DOT_DIRECTORY) return null

        return if (fileAttributes == FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) {
            val path = if (fileName == DOT_DIRECTORY) {
                parentPath.substringBeforeLast(SEPARATOR)
            } else {
                parentPath + SEPARATOR + fileName
            }

            NetworkDirectory(fileName, path)
        } else {
            val localPath = fileManager.getLocalFilePath(server, sharedPath, parentPath)
            val localFile = fileManager.getLocalFileRef(server, sharedPath, parentPath, fileName)
            val isLocal = localFile.exists()

            NetworkFile(fileName, localPath, isLocal)
        }
    }
}
