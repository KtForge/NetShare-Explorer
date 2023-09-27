package com.msd.data.explorer_data.mapper

import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory

private const val DOT_DIRECTORY = "."
private const val DOUBLE_DOT_DIRECTORY = ".."
private const val SEPARATOR = "\\"

object FilesAndDirectoriesMapper : IFilesAndDirectoriesMapper {

    override fun buildFilesResult(
        server: String,
        sharedPath: String,
        parentPath: String,
        files: List<FileIdBothDirectoryInformation>,
        fileManager: FileManager,
    ): FilesResult {
        val rootPath = "\\\\$server$SEPARATOR$sharedPath"

        val workingDirectoryAbsolutePath = rootPath + parentPath

        val parentDirectoryPath = parentPath.substringBeforeLast(SEPARATOR)
        val parentDirectoryAbsolutePath = rootPath + parentDirectoryPath

        val isParentDirectoryRoot =
            workingDirectoryAbsolutePath.substringAfterLast(rootPath).isEmpty()

        val parentDirectory = if (isParentDirectoryRoot) {
            null
        } else {
            ParentDirectory(DOUBLE_DOT_DIRECTORY, parentDirectoryPath, parentDirectoryAbsolutePath)
        }

        val workingDirectory = WorkingDirectory(parentPath, workingDirectoryAbsolutePath)

        val filesAndDirectories = files.mapNotNull { file ->
            file.toBaseFile(server, sharedPath, parentPath, fileManager)
        }
            .sortedBy { fileOrDirectory -> fileOrDirectory.name }
            .sortedBy { fileOrDirectory -> fileOrDirectory is NetworkFile }

        return FilesResult(parentDirectory, workingDirectory, filesAndDirectories)
    }

    // Generate all structure of files & directories
    private fun FileIdBothDirectoryInformation.toBaseFile(
        server: String,
        sharedPath: String,
        parentPath: String,
        fileManager: FileManager,
    ): IBaseFile? {
        if (fileName == DOT_DIRECTORY || fileName == DOUBLE_DOT_DIRECTORY) return null

        return if (fileAttributes == FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) {
            val path = parentPath + SEPARATOR + fileName
            val absolutePath = "\\\\$server$SEPARATOR$sharedPath$parentPath$SEPARATOR$fileName"

            NetworkDirectory(fileName, path, absolutePath = absolutePath)
        } else {
            val localPath = fileManager.getLocalFilePath(server, sharedPath, parentPath)
            val localFile = fileManager.getLocalFile(localPath, fileName)
            val isLocal = localFile.exists()

            NetworkFile(fileName, parentPath, localPath, isLocal)
        }
    }
}
