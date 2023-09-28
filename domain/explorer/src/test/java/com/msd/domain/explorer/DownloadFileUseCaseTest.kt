package com.msd.domain.explorer

import com.msd.domain.explorer.model.SMBException
import com.msd.core.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadFileUseCaseTest : CoroutineTest() {

    private val repository: IExplorerRepository = mock()

    private val useCase = DownloadFileUseCase(repository)

    @Test
    fun `when downloading file should invoke the repository`() = runTest {
        execute()

        verify(repository).downloadFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when connection error while downloading file should invoke the repository`() = runTest {
        val expectedException = SMBException.ConnectionError
        whenever(
            repository.downloadFile(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "Name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(expectedException)

        try {
            execute()
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(repository).downloadFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when access error while downloading file should invoke the repository`() = runTest {
        val expectedException = SMBException.AccessDenied
        whenever(
            repository.downloadFile(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "Name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(expectedException)

        try {
            execute()
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(repository).downloadFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when cancel error while downloading file should invoke the repository`() = runTest {
        val expectedException = SMBException.CancelException
        whenever(
            repository.downloadFile(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "Name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(expectedException)

        try {
            execute()
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(repository).downloadFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when unknown error while downloading file should invoke the repository`() = runTest {
        val expectedException = SMBException.UnknownError
        whenever(
            repository.downloadFile(
                server = "Server",
                sharedPath = "SharedPath",
                fileName = "Name",
                filePath = "path",
                localFilePath = "localPath",
                user = "User",
                psw = "Psw"
            )
        ).thenThrow(expectedException)

        try {
            execute()
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(repository).downloadFile(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
        verifyNoMoreInteractions(repository)
    }

    private suspend fun execute() {
        useCase.invoke(
            server = "Server",
            sharedPath = "SharedPath",
            fileName = "Name",
            filePath = "path",
            localFilePath = "localPath",
            user = "User",
            psw = "Psw"
        )
    }
}