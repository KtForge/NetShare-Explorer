package com.msd.feature.explorer.helper

import com.msd.domain.explorer.DeleteFileUseCase
import com.msd.domain.explorer.DownloadFileUseCase
import com.msd.domain.explorer.GetFilesAndDirectoriesUseCase
import com.msd.domain.explorer.OpenFileUseCase
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.SMBException
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.core.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.mockito.stubbing.OngoingStubbing
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FilesAndDirectoriesHelperTest : CoroutineTest() {

    private val getFilesAndDirectoriesUseCase: GetFilesAndDirectoriesUseCase = mock()
    private val downloadFileUseCase: DownloadFileUseCase = mock()
    private val openFileUseCase: OpenFileUseCase = mock()
    private val deleteFileUseCase: DeleteFileUseCase = mock()

    private val helper = FilesAndDirectoriesHelper(
        getFilesAndDirectoriesUseCase,
        downloadFileUseCase,
        openFileUseCase,
        deleteFileUseCase
    )

    private val smbConfiguration = SMBConfiguration(
        id = 0,
        name = "Name",
        server = "Server",
        sharedPath = "SharedPath",
        user = "User",
        psw = "Psw",
    )
    private val file = NetworkFile("File", "path", "localPath", isLocal = false)

    @Test
    fun `when getting files and directories should return the expected list`() = runTest {
        val expectedResult: FilesResult = mock()
        whenGetFilesAndDirectories { thenReturn(expectedResult) }

        val result = helper.getFilesAndDirectories(smbConfiguration, path = "path")

        assert(result == expectedResult)
        verify(getFilesAndDirectoriesUseCase).invoke(
            "Server",
            "SharedPath",
            "path",
            "User",
            "Psw"
        )
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(openFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when getting files and directories with connection error should return the exception`() =
        runTest {
            val expectedException = SMBException.ConnectionError
            whenGetFilesAndDirectories { thenThrow(expectedException) }

            try {
                helper.getFilesAndDirectories(smbConfiguration, path = "path")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(getFilesAndDirectoriesUseCase).invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
            verifyNoInteractions(downloadFileUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when getting files and directories with access error should return the exception`() =
        runTest {
            val expectedException = SMBException.AccessDenied
            whenGetFilesAndDirectories { thenThrow(expectedException) }

            try {
                helper.getFilesAndDirectories(smbConfiguration, path = "path")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(getFilesAndDirectoriesUseCase).invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
            verifyNoInteractions(downloadFileUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when getting files and directories with cancellation error should return the exception`() =
        runTest {
            val expectedException = SMBException.CancelException
            whenGetFilesAndDirectories { thenThrow(expectedException) }

            try {
                helper.getFilesAndDirectories(smbConfiguration, path = "path")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(getFilesAndDirectoriesUseCase).invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
            verifyNoInteractions(downloadFileUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when getting files and directories with unknown error should return the exception`() =
        runTest {
            val expectedException = SMBException.UnknownError
            whenGetFilesAndDirectories { thenThrow(expectedException) }

            try {
                helper.getFilesAndDirectories(smbConfiguration, path = "path")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(getFilesAndDirectoriesUseCase).invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
            verifyNoInteractions(downloadFileUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when downloading file should invoke the expected use case`() = runTest {
        helper.downloadFile(smbConfiguration, file)

        verify(downloadFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(openFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when downloading file and connection error should throw the expected exception`() =
        runTest {
            val expectedException = SMBException.ConnectionError
            whenDownloadFile { thenThrow(expectedException) }

            try {
                helper.downloadFile(smbConfiguration, file)
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(downloadFileUseCase).invoke(
                "Server",
                "SharedPath",
                "File",
                "path",
                "localPath",
                "User",
                "Psw",
            )
            verifyNoInteractions(getFilesAndDirectoriesUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when downloading file and access error should throw the expected exception`() = runTest {
        val expectedException = SMBException.AccessDenied
        whenDownloadFile { thenThrow(expectedException) }

        try {
            helper.downloadFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(downloadFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(openFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when downloading file and cancellation error should throw the expected exception`() =
        runTest {
            val expectedException = SMBException.CancelException
            whenDownloadFile { thenThrow(expectedException) }

            try {
                helper.downloadFile(smbConfiguration, file)
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(downloadFileUseCase).invoke(
                "Server",
                "SharedPath",
                "File",
                "path",
                "localPath",
                "User",
                "Psw",
            )
            verifyNoInteractions(getFilesAndDirectoriesUseCase)
            verifyNoInteractions(openFileUseCase)
            verifyNoInteractions(deleteFileUseCase)
        }

    @Test
    fun `when downloading file and unknown error should throw the expected exception`() = runTest {
        val expectedException = SMBException.UnknownError
        whenDownloadFile { thenThrow(expectedException) }

        try {
            helper.downloadFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(downloadFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(openFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file should return the expected file`() = runTest {
        val expectedResult: File = mock()
        whenOpenFile { thenReturn(expectedResult) }

        val result = helper.openFile(smbConfiguration, file)

        assert(result == expectedResult)
        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file and return null should return the expected file`() = runTest {
        whenOpenFile { thenReturn(null) }

        val result = helper.openFile(smbConfiguration, file)

        assert(result == null)
        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file and connection error should throw the error`() = runTest {
        val expectedException = SMBException.ConnectionError
        whenOpenFile { thenThrow(expectedException) }

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file and access error should throw the error`() = runTest {
        val expectedException = SMBException.AccessDenied
        whenOpenFile { thenThrow(expectedException) }

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file and cancellation error should throw the error`() = runTest {
        val expectedException = SMBException.CancelException
        whenOpenFile { thenThrow(expectedException) }

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when opening file and unknown error should throw the error`() = runTest {
        val expectedException = SMBException.UnknownError
        whenOpenFile { thenThrow(expectedException) }

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(openFileUseCase).invoke(
            "Server",
            "SharedPath",
            "File",
            "path",
            "localPath",
            "User",
            "Psw",
        )
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(deleteFileUseCase)
    }

    @Test
    fun `when deleting file should invoke the use case`() {
        helper.deleteFile(file)

        verify(deleteFileUseCase).invoke(file)
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verifyNoInteractions(downloadFileUseCase)
        verifyNoInteractions(openFileUseCase)
    }

    private suspend fun whenGetFilesAndDirectories(result: OngoingStubbing<FilesResult>.() -> Unit) {
        whenever(
            getFilesAndDirectoriesUseCase.invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
        ).result()
    }

    private suspend fun whenDownloadFile(result: OngoingStubbing<Unit>.() -> Unit) {
        whenever(
            downloadFileUseCase.invoke(
                "Server",
                "SharedPath",
                "File",
                "path",
                "localPath",
                "User",
                "Psw",
            )
        ).result()
    }

    private suspend fun whenOpenFile(result: OngoingStubbing<File>.() -> Unit) {
        whenever(
            openFileUseCase.invoke(
                "Server",
                "SharedPath",
                "File",
                "path",
                "localPath",
                "User",
                "Psw",
            )
        ).result()
    }
}
