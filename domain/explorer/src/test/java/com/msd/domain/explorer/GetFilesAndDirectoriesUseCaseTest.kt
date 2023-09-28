package com.msd.domain.explorer

import com.msd.domain.explorer.model.FilesResult
import com.msd.core.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetFilesAndDirectoriesUseCaseTest : CoroutineTest() {

    private val repository: IExplorerRepository = mock()
    private val useCase = GetFilesAndDirectoriesUseCase(repository)

    @Test
    fun `when getting files and directories should invoke the repository`() = runTest {
        val filesResult: FilesResult = mock()
        whenever(
            repository.retrieveFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                directoryPath = "path",
                user = "User",
                psw = "Psw"
            )
        ).thenReturn(filesResult)

        val result = useCase(
            server = "Server",
            sharedPath = "SharedPath",
            directoryPath = "path",
            user = "User",
            psw = "Psw",
        )

        verify(repository).retrieveFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            directoryPath = "path",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
        assert(result == filesResult)
    }

    @Test
    fun `when exception while getting files and directories should return the error`() = runTest {
        val exception: Exception = mock()
        whenever(
            repository.retrieveFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                directoryPath = "path",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(exception)

        try {
            useCase(
                server = "Server",
                sharedPath = "SharedPath",
                directoryPath = "path",
                user = "User",
                psw = "Psw",
            )
            assert(false)
        } catch (e: Exception) {
            assert(e == exception)
        }
        verify(repository).retrieveFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            directoryPath = "path",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }
}
