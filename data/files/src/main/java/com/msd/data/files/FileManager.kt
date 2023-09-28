package com.msd.data.files

import android.content.Context
import com.msd.domain.explorer.model.IBaseFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject

class FileManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val cacheDir = context.cacheDir

    // Builds the path to a specific directory in the cache directory
    // Example: cache/192.168.1.1/Public/User/Data
    private fun getCacheDirectoryPath(
        server: String,
        sharedPath: String,
        directory: String
    ): String = "${cacheDir.absolutePath}/$server/$sharedPath/" + directory

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
            val files = cacheServerDirectory.listFiles()

            if (files.isNullOrEmpty()) {
                cacheServerDirectory.deleteRecursively()
            } else {
                files.filter { it.isFile }.forEach { file ->
                    if (filesAndDirectories.all { file.name != it.name }) {
                        deleteFile(serverDirectoryPath, file.name)
                    }
                }

                files.filter { it.isDirectory }.forEach { directory ->
                    if (directory.listFiles().isNullOrEmpty()) {
                        directory.delete()
                    }
                }
            }
        }
    }

    // Creates the path to a new or existing local file
    // Example: cache/192.168.1.1/Public/User/Data/text.txt
    fun getLocalFilePath(
        server: String,
        sharedPath: String,
        directory: String,
    ): String {
        return getCacheDirectoryPath(server, sharedPath, directory)
    }

    // Make the directories for a new file.
    // Example: cache/192.168.1.1/Public/User/Data
    fun makeDirectoriesForNewFile(
        server: String,
        sharedPath: String,
        directory: String
    ) {
        val path = getCacheDirectoryPath(server, sharedPath, directory)
        val directoryFile = File(path)

        if (!directoryFile.exists()) {
            directoryFile.mkdirs()
        }
    }

    fun getLocalFile(localFilePath: String, fileName: String) = File(localFilePath, fileName)

    suspend fun copyFile(inputStream: InputStream, outFile: File) {
        val out = outFile.outputStream()
        var bytesCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = inputStream.read(buffer)

        while (bytes >= 0) {
            yield()
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            // progressListener(bytesCopied.toFloat().div(fileSize))
            bytes = inputStream.read(buffer)
        }
    }

    // Returns the creation date of a file in milliseconds
    fun getCreationTimeMillis(file: File): Long {
        val fileData = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)

        return fileData.creationTime().toMillis()
    }

    fun deleteFile(filePath: String, fileName: String) {
        val file = File(filePath, fileName)

        if (file.exists()) {
            file.delete()
        }
    }

    fun deleteServerContents(server: String, sharedPath: String) {
        val directoryPath = getCacheDirectoryPath(server, sharedPath, directory = "")
        val directory = File(directoryPath)

        if (directory.exists()) {
            directory.deleteRecursively()
        }
    }
}
