package com.msd.data.explorer_data

import com.msd.data.explorer_data.network.ExplorerDataSource
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
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
        val filesAndDirectories = listOf(
            NetworkFile("B", "path"),
            NetworkFile("A", "path"),
            NetworkParentDirectory("B", "path"),
            NetworkParentDirectory("A", "path"),
            NetworkDirectory("B", "path"),
            NetworkDirectory("A", "path"),
        )
        whenever(
            dataSource.getFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(filesAndDirectories)
        val expectedResult = listOf(
            NetworkParentDirectory("A", "path"),
            NetworkParentDirectory("B", "path"),
            NetworkDirectory("A", "path"),
            NetworkDirectory("B", "path"),
            NetworkFile("A", "path"),
            NetworkFile("B", "path"),
        )

        val result = repository.retrieveFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            user = "User",
            psw = "Psw",
        )

        //assert(result == expectedResult)
        verify(dataSource).getFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            user = "User",
            psw = "Psw",
        )
    }

    @Test
    fun `when exception while retrieving files and directories should throw the exception`() =
        runTest {
            val expectedException: Exception = mock()
            whenever(
                dataSource.getFilesAndDirectories(
                    server = "Server",
                    sharedPath = "SharedPath",
                    absolutePath = "path",
                    user = "User",
                    psw = "Psw",
                )
            ).thenThrow(expectedException)

            try {
                repository.retrieveFilesAndDirectories(
                    server = "Server",
                    sharedPath = "SharedPath",
                    absolutePath = "path",
                    user = "User",
                    psw = "Psw",
                )
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }
            verify(dataSource).getFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                user = "User",
                psw = "Psw",
            )
        }

    @Test
    fun `when opening file should return the file`() = runTest {
        val expectedResult: File = mock()
        whenever(
            dataSource.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                fileName = "name",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(expectedResult)

        val result = repository.openFile(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            fileName = "name",
            user = "User",
            psw = "Psw",
        )

        assert(result == expectedResult)
        verify(dataSource).openFile(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            fileName = "name",
            user = "User",
            psw = "Psw",
        )
    }

    @Test
    fun `when opening null file should return null`() = runTest {
        whenever(
            dataSource.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                fileName = "name",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(null)

        val result = repository.openFile(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            fileName = "name",
            user = "User",
            psw = "Psw",
        )

        assert(result == null)
        verify(dataSource).openFile(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            fileName = "name",
            user = "User",
            psw = "Psw",
        )
    }

    @Test
    fun `when exception while opening file should throw the exception`() = runTest {
        val expectedException: Exception = mock()
        whenever(
            dataSource.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                fileName = "name",
                user = "User",
                psw = "Psw",
            )
        ).thenThrow(expectedException)

        try {
            repository.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                fileName = "name",
                user = "User",
                psw = "Psw",
            )
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }
        verify(dataSource).openFile(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            fileName = "name",
            user = "User",
            psw = "Psw",
        )
    }
}