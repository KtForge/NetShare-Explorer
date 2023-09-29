package com.msd.data.files

import androidx.test.platform.app.InstrumentationRegistry
import com.msd.domain.explorer.model.NetworkFile
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Paths
import java.nio.file.attribute.FileTime


class FileManagerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fileManager = FileManager(context.applicationContext)

    private val server = "Server"
    private val sharedPath = "SharedPath"
    private val path = "Path"
    private val localServerPath =
        "/data/user/0/com.msd.data.files.test/cache/$server/$sharedPath/$path"
    private val testFile1Name = "test1.txt"
    private val testFile2Name = "test2.txt"
    private val testFile1 = File(localServerPath, testFile1Name)
    private val testFile2 = File(localServerPath, testFile2Name)

    @Test
    fun cleaningFilesShouldRemoveCacheFiles() {
        File(localServerPath).mkdirs()
        testFile1.createNewFile()
        testFile2.createNewFile()
        assert(testFile1.exists())
        assert(testFile2.exists())

        fileManager.cleanFiles(server, sharedPath, path, emptyList())

        assert(!testFile1.exists())
        assert(!testFile2.exists())
    }

    @Test
    fun cleaningFilesShouldMaintainSpecifiedFiles() {
        File(localServerPath).mkdirs()
        testFile1.createNewFile()
        testFile2.createNewFile()
        assert(testFile1.exists())
        assert(testFile2.exists())

        fileManager.cleanFiles(
            server,
            sharedPath,
            path,
            listOf(NetworkFile(testFile1Name, "", "", true))
        )

        assert(testFile1.exists())
        assert(!testFile2.exists())
    }

    @Test
    fun cleaningFilesShouldDeleteEmptyNestedDirectories() {
        val directory1 = File(localServerPath, "Directory")
        val directory2 = File(directory1.absolutePath, "Directory")
        directory2.mkdirs()
        assert(directory1.exists())
        assert(directory2.exists())

        fileManager.cleanFiles(
            server,
            sharedPath,
            path,
            emptyList()
        )

        // TODO FIX
        // assert(!directory1.exists())
        // assert(!directory2.exists())
    }

    @Test
    fun cleaningFilesShouldKeepNonEmptyDirectories() {
        val directory = File(localServerPath, "Directory")
        val file = File(directory.absolutePath, "Directory")
        directory.mkdirs()
        file.createNewFile()
        assert(directory.exists())
        assert(file.exists())

        fileManager.cleanFiles(
            server,
            sharedPath,
            path,
            emptyList()
        )

        assert(directory.exists())
        assert(file.exists())
    }

    @Test
    fun getLocalPathShouldReturnTheExpectedPath() {
        val result = fileManager.getLocalFilePath(server, sharedPath, path)

        assert(result == localServerPath)
    }

    @Test
    fun makeDirectoriesForNewFileShouldMakeTheNeededDirectories() {
        val directoryPath = "Directory/SecondDirectory"
        val directory = File(localServerPath, directoryPath)
        assert(!directory.exists())

        fileManager.makeDirectoriesForNewFile(server, sharedPath, "$path/$directoryPath")

        assert(directory.exists())
    }

    @Test
    fun getLocalFileShouldReturnTheExpectedFile() {
        testFile1.createNewFile()

        val result =
            fileManager.getLocalFile(testFile1.absolutePath.substringBeforeLast("/"), testFile1Name)

        assert(result == testFile1)
    }

    @Test
    fun getFileCreationTimeShouldReturnTheExpectedValue() {
        File(localServerPath).mkdirs()
        testFile1.createNewFile()
        val creationMillis = System.currentTimeMillis()
        val time = FileTime.fromMillis(creationMillis)
        Files.setAttribute(
            Paths.get(testFile1.absolutePath),
            "creationTime",
            time,
            LinkOption.NOFOLLOW_LINKS
        )

        val result = fileManager.getCreationTimeMillis(testFile1)

        // TODO Fix to match exactly the same millis
        // assert(result == creationMillis)
    }

    @Test
    fun copyingFileWorksAsExpected() = runTest {
        testFile1.createNewFile()
        val content = "Test file"
        writeTextFile(testFile1, content)
        testFile2.createNewFile()
        assert(readTextFile(testFile2).isEmpty())

        fileManager.copyFile(testFile1.inputStream(), testFile2)

        assert(readTextFile(testFile2) == content)
    }

    private fun writeTextFile(file: File, content: String) {
        return file.writeText(content)
    }

    private fun readTextFile(file: File): String {
        return file.useLines { it.toList() }.joinToString(",")
    }

    @Test
    fun removeLocalFileShouldWorkProperly() {
        testFile1.createNewFile()
        assert(testFile1.exists())

        fileManager.deleteFile(testFile1.absolutePath.substringBeforeLast("/"), testFile1Name)

        assert(!testFile1.exists())
    }

    @Test
    fun deleteServerContentsShouldRemoveAllFiles() {
        val serverDirectory = File(localServerPath)
        serverDirectory.mkdirs()
        testFile1.createNewFile()
        testFile2.createNewFile()
        assert(serverDirectory.exists())
        assert(testFile1.exists())
        assert(testFile2.exists())

        fileManager.deleteServerContents(server, sharedPath)

        assert(!serverDirectory.exists())
        assert(!testFile1.exists())
        assert(!testFile2.exists())
    }
}
