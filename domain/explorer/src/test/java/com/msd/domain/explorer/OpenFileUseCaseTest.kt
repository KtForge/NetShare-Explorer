package com.msd.domain.explorer

import com.msd.domain.explorer.IExplorerRepository
import com.msd.domain.explorer.OpenFileUseCase
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class OpenFileUseCaseTest : CoroutineTest() {

    private val repository: IExplorerRepository = mock()
    private val useCase = OpenFileUseCase(repository)

    @Test
    fun `when opening file should return the expected file`() = runTest {
        val expectedResult: File = mock()
        whenever(
            repository.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                directoryRelativePath = "path",
                fileName = "file",
                user = "User",
                psw = "Psw",
            )
        ).thenReturn(expectedResult)

        val result = useCase(
            server = "Server",
            sharedPath = "SharedPath",
            directoryRelativePath = "path",
            fileName = "file",
            user = "User",
            psw = "Psw",
        )

        assert(result == expectedResult)
        verify(repository).openFile(
            server = "Server",
            sharedPath = "SharedPath",
            directoryRelativePath = "path",
            fileName = "file",
            user = "User",
            psw = "Psw",
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when exception while opening file should return the expected error`() = runTest {
        val expectedResult: Exception = mock()
        whenever(
            repository.openFile(
                server = "Server",
                sharedPath = "SharedPath",
                directoryRelativePath = "path",
                fileName = "file",
                user = "User",
                psw = "Psw",
            )
        ).thenThrow(expectedResult)

        try {
            useCase(
                server = "Server",
                sharedPath = "SharedPath",
                directoryRelativePath = "path",
                fileName = "file",
                user = "User",
                psw = "Psw",
            )
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedResult)
        }

        verify(repository).openFile(
            server = "Server",
            sharedPath = "SharedPath",
            directoryRelativePath = "path",
            fileName = "file",
            user = "User",
            psw = "Psw",
        )
        verifyNoMoreInteractions(repository)
    }
}
