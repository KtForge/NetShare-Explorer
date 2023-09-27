package com.msd.data.explorer_data.mapper

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

class FilesAndDirectoriesMapperTest {

    private val mapper = FilesAndDirectoriesMapper

    private val server = "Server"
    private val sharedPath = "SharedPath"
    private val parentPath = "\\ParentPath"
    private val file: FileIdBothDirectoryInformation = mock {
        on { fileName } doReturn "Name"
        on { fileAttributes } doReturn 22
    }
    private val directory: FileIdBothDirectoryInformation = mock {
        on { fileName } doReturn "Directory"
        on { fileAttributes } doReturn 16
    }
    private val parentDirectory: FileIdBothDirectoryInformation = mock {
        on { fileName } doReturn ".."
    }
    private val workingDirectory: FileIdBothDirectoryInformation = mock {
        on { fileName } doReturn "."
    }
    private val files = listOf(file, directory, parentDirectory, workingDirectory)
    private val localFile: File = mock {
        on { exists() } doReturn true
    }
    private val fileManager: FileManager = mock {
        on { getLocalFile("path", "Name") } doReturn localFile
    }

    @Test
    fun `when building files result with parent directory should return the expected data`() {
        whenever(fileManager.getLocalFilePath(server, sharedPath, parentPath)).thenReturn("path")
        val networkDirectory = NetworkDirectory(
            "Directory",
            "$parentPath\\Directory",
            "\\\\Server\\SharedPath\\ParentPath\\Directory"
        )
        val networkFile = NetworkFile("Name", parentPath, "path", true)
        val expectedResult = FilesResult(
            parentDirectory = ParentDirectory(
                name = "..",
                path = "",
                absolutePath = "\\\\Server\\SharedPath",
            ),
            workingDirectory = WorkingDirectory(
                path = "\\ParentPath",
                absolutePath = "\\\\Server\\SharedPath\\ParentPath",
            ),
            filesAndDirectories = listOf(networkDirectory, networkFile)
        )

        val result = mapper.buildFilesResult(server, sharedPath, parentPath, files, fileManager)

        assert(result == expectedResult)
    }

    @Test
    fun `when building files result without parent directory should return the expected data`() {
        whenever(
            fileManager.getLocalFilePath(
                server,
                sharedPath,
                directory = ""
            )
        ).thenReturn("path")
        val networkDirectory = NetworkDirectory(
            "Directory",
            "\\Directory",
            "\\\\Server\\SharedPath\\Directory"
        )
        val networkFile = NetworkFile("Name", "", "path", true)
        val expectedResult = FilesResult(
            parentDirectory = null,
            workingDirectory = WorkingDirectory(
                path = "",
                absolutePath = "\\\\Server\\SharedPath",
            ),
            filesAndDirectories = listOf(networkDirectory, networkFile)
        )

        val result = mapper.buildFilesResult(
            server,
            sharedPath,
            parentPath = "",
            files,
            fileManager
        )

        assert(result == expectedResult)
    }
}