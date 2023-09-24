package com.msd.data.explorer_data

import com.msd.data.explorer_data.network.ExplorerDataSource
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorerRepositoryTest : CoroutineTest() {

    private val dataSource: ExplorerDataSource = mock()
    private val repository = ExplorerRepository(dataSource)

    @Test
    fun `when retrieving files and directories should return the expected data`() = runTest {
        val parentDirectory = ParentDirectory("..", "path", "absolutePath")
        val workingDirectory = WorkingDirectory("path", "absolutePath")
        val filesAndDirectories = listOf(
            NetworkFile("B", "path", "localPath", isLocal = false),
            NetworkFile("A", "path", "localPath", isLocal = false),
            NetworkDirectory("B", "path", "absolutePath"),
            NetworkDirectory("A", "path", "absolutePath"),
        )
        val filesResult = FilesResult(parentDirectory, workingDirectory, filesAndDirectories)
        whenever(
            dataSource.getFilesResult(
                server = "Server",
                sharedPath = "SharedPath",
                directoryPath = "path",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(filesResult)

        val result = repository.retrieveFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            directoryPath = "path",
            user = "User",
            psw = "Psw",
        )

        assert(result == filesResult)
        verify(dataSource).getFilesResult(
            server = "Server",
            sharedPath = "SharedPath",
            directoryPath = "path",
            user = "User",
            psw = "Psw",
        )
    }

    @Test
    fun `when exception while retrieving files and directories should throw the exception`() =
        runTest {
            val expectedException: Exception = mock()
            whenever(
                dataSource.getFilesResult(
                    server = "Server",
                    sharedPath = "SharedPath",
                    directoryPath = "path",
                    user = "User",
                    psw = "Psw",
                )
            ).thenThrow(expectedException)

            try {
                repository.retrieveFilesAndDirectories(
                    server = "Server",
                    sharedPath = "SharedPath",
                    directoryPath = "path",
                    user = "User",
                    psw = "Psw",
                )
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }
            verify(dataSource).getFilesResult(
                server = "Server",
                sharedPath = "SharedPath",
                directoryPath = "path",
                user = "User",
                psw = "Psw",
            )
        }

    @Test
    fun `when opening file should return the file`() = runTest {
        val expectedResult: File = mock()
        whenever(
            dataSource.isLocalFileValid(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(true)
        whenever(dataSource.openFile(fileName = "name", localFilePath = "localPath"))
            .thenReturn(expectedResult)

        val result = repository.openFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw",
        )

        assert(result == expectedResult)
        verify(dataSource).isLocalFileValid(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw",
        )
        verify(dataSource).openFile(fileName = "name", localFilePath = "localPath")
    }

    @Test
    fun `when opening null file should return null`() = runTest {
        whenever(
            dataSource.isLocalFileValid(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(true)
        whenever(dataSource.openFile(fileName = "name", localFilePath = "localPath"))
            .thenReturn(null)

        val result = repository.openFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw",
        )

        assert(result == null)
        verify(dataSource).isLocalFileValid(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw",
        )
        verify(dataSource).openFile(fileName = "name", localFilePath = "localPath")
    }

    @Test
    fun `when exception while opening file should throw the exception`() = runTest {
        val expectedException: Exception = mock()
        whenever(
            dataSource.isLocalFileValid(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(true)
        whenever(dataSource.openFile(fileName = "name", localFilePath = "localPath"))
            .thenThrow(expectedException)

        try {
            repository.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw",
            )
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }
        verify(dataSource).isLocalFileValid(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw",
        )
        verify(dataSource).openFile(fileName = "name", localFilePath = "localPath")
    }
}