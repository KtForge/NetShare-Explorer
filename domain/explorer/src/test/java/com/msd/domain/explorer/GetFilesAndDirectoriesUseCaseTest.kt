package com.msd.domain.explorer

import com.msd.domain.explorer.GetFilesAndDirectoriesUseCase
import com.msd.domain.explorer.IExplorerRepository
import com.msd.domain.explorer.model.IBaseFile
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
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
        val filesAndDirectories: List<IBaseFile> = mock()
        whenever(
            repository.retrieveFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                user = "User",
                psw = "Psw"
            )
        ).thenReturn(filesAndDirectories)

        val result = useCase(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            user = "User",
            psw = "Psw",
        )

        verify(repository).retrieveFilesAndDirectories(
            server = "Server",
            sharedPath = "SharedPath",
            absolutePath = "path",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
        assert(result == filesAndDirectories)
    }

    @Test
    fun `when exception while getting files and directories should return the error`() = runTest {
        val exception: Exception = mock()
        whenever(
            repository.retrieveFilesAndDirectories(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(exception)

        try {
            useCase(
                server = "Server",
                sharedPath = "SharedPath",
                absolutePath = "path",
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
            absolutePath = "path",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }
}
