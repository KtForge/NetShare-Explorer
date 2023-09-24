package com.msd.feature.explorer.helper

import com.msd.domain.explorer.DeleteFileUseCase
import com.msd.domain.explorer.DownloadFileUseCase
import com.msd.domain.explorer.GetFilesAndDirectoriesUseCase
import com.msd.domain.explorer.OpenFileUseCase
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.SMBException
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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
        whenever(
            getFilesAndDirectoriesUseCase.invoke(
                "Server",
                "SharedPath",
                "path",
                "User",
                "Psw"
            )
        ).thenReturn(expectedResult)

        val result = helper.getFilesAndDirectories(smbConfiguration, path = "path")

        assert(result == expectedResult)
        verify(getFilesAndDirectoriesUseCase).invoke(
            "Server",
            "SharedPath",
            "path",
            "User",
            "Psw"
        )
    }

    @Test
    fun `when getting files and directories with connection error should return the exception`() =
        runTest {
            val expectedException = SMBException.ConnectionError
            whenever(
                getFilesAndDirectoriesUseCase.invoke(
                    "Server",
                    "SharedPath",
                    "path",
                    "User",
                    "Psw"
                )
            ).thenThrow(expectedException)

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
        }

    @Test
    fun `when getting files and directories with access error should return the exception`() =
        runTest {
            val expectedException = SMBException.AccessDenied
            whenever(
                getFilesAndDirectoriesUseCase.invoke(
                    "Server",
                    "SharedPath",
                    "path",
                    "User",
                    "Psw"
                )
            ).thenThrow(expectedException)

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
        }

    @Test
    fun `when getting files and directories with unknown error should return the exception`() =
        runTest {
            val expectedException = SMBException.UnknownError
            whenever(
                getFilesAndDirectoriesUseCase.invoke(
                    "Server",
                    "SharedPath",
                    "path",
                    "User",
                    "Psw"
                )
            ).thenThrow(expectedException)

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
        }

    @Test
    fun `when opening file should return the expected file`() = runTest {
        val expectedResult: File = mock()
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
        ).thenReturn(expectedResult)

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
    }

    @Test
    fun `when opening file and return null should return the expected file`() = runTest {
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
        ).thenReturn(null)

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
    }

    @Test
    fun `when opening file and connection error should throw the error`() = runTest {
        val expectedException = SMBException.ConnectionError
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
        ).thenThrow(expectedException)

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }
    }

    @Test
    fun `when opening file and access error should throw the error`() = runTest {
        val expectedException = SMBException.AccessDenied
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
        ).thenThrow(expectedException)

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }
    }

    @Test
    fun `when opening file and unknown error should throw the error`() = runTest {
        val expectedException = SMBException.UnknownError
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
        ).thenThrow(expectedException)

        try {
            helper.openFile(smbConfiguration, file)
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }
    }
}
