package com.msd.data.files

import android.content.Context
import com.msd.domain.explorer.model.IBaseFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject

class FileManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val cacheDir = context.cacheDir

    // Builds the path to a specific shared path in the cache directory
    // Example: cache/192.168.1.1/Public/
    private fun getCacheServerPath(server: String, sharedPath: String): String {
        return "${cacheDir.absolutePath}/$server/$sharedPath/"
    }

    // Builds the path to a specific directory in the cache directory
    // Example: cache/192.168.1.1/Public/User/Data
    private fun getCacheDirectoryPath(
        server: String,
        sharedPath: String,
        directory: String
    ): String = getCacheServerPath(server, sharedPath) + directory

    // Deletes files that don't exist on the remote ($filesAndDirectories, only files)
    fun cleanFiles(
        server: String,
        sharedPath: String,
        path: String,
        filesAndDirectories: List<IBaseFile>
    ) {
        val serverDirectoryPath = getCacheDirectoryPath(server, sharedPath, path)
        val cacheServerDirectory = File(serverDirectoryPath)

        if (cacheServerDirectory.exists()) {
            cacheServerDirectory.listFiles()?.filter { it.isFile }?.forEach { file ->
                if (filesAndDirectories.all { file.name != it.name }) {
                    file.delete()
                }
            }
        }
    }

    // Make the directories for a new file.
    // Example: cache/192.168.1.1/Public/User/Data
    private fun makeDirectoriesForNewFile(path: String) {
        val directory = File(path)

        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    // Creates the path to a new or existing local file
    // Example: cache/192.168.1.1/Public/User/Data/text.txt
    fun getLocalFilePath(
        server: String,
        sharedPath: String,
        directory: String,
        fileName: String
    ): String {
        return getCacheDirectoryPath(server, sharedPath, directory) + "/" + fileName
    }

    // Creates a reference to a new or existing local file
    // Example: cache/192.168.1.1/Public/User/Data/text.txt
    fun getLocalFileRef(
        server: String,
        sharedPath: String,
        directory: String,
        fileName: String
    ): File {
        val path = getCacheDirectoryPath(server, sharedPath, directory)
        makeDirectoriesForNewFile(path)

        return File(path, fileName)
    }

    // Returns the creation date of a file in milliseconds
    fun getCreationTimeMillis(file: File): Long {
        val fileData = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)

        return fileData.creationTime().toMillis()
    }

    fun deleteFile(filePath: String) {
        val file = File(filePath)

        if (file.exists()) {
            file.delete()
        }
    }
}