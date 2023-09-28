package com.msd.data.explorer_data.network

import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.SMBException
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorerDataSourceTest : CoroutineTest() {

    private val fileManager: FileManager = mock()
    private val smbHelper: SMBHelper = mock()
    private val explorerTracker: ExplorerTracker = mock()
    private val dataSource = ExplorerDataSource(smbHelper, fileManager, explorerTracker)

    private val server = "192.168.1.1"
    private val sharedPath = "Public"

    @Test
    fun `when retrieving files and directories should return the expected data`() = runTest {
        val expectedResult: FilesResult = mock()
        whenever(smbHelper.onConnection<FilesResult>(any(), any(), any(), any(), any()))
            .thenReturn(expectedResult)

        val result = dataSource.getFilesResult(server, sharedPath, "", "", "")

        assert(result == expectedResult)
        verify(smbHelper).onConnection<FilesResult>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when connection error while retrieving files and directories should return the expected data`() =
        runTest {
            val expectedException = SMBException.ConnectionError
            whenever(smbHelper.onConnection<FilesResult>(any(), any(), any(), any(), any()))
                .thenThrow(expectedException)

            try {
                dataSource.getFilesResult(server, sharedPath, "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<FilesResult>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
            verifyNoInteractions(fileManager)
        }

    @Test
    fun `when access error while retrieving files and directories should return the expected data`() =
        runTest {
            val expectedException = SMBException.AccessDenied
            whenever(smbHelper.onConnection<FilesResult>(any(), any(), any(), any(), any()))
                .thenThrow(expectedException)

            try {
                dataSource.getFilesResult(server, sharedPath, "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<FilesResult>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
            verifyNoInteractions(fileManager)
        }

    @Test
    fun `when cancellation error while retrieving files and directories should return the expected data`() =
        runTest {
            val expectedException = SMBException.CancelException
            whenever(smbHelper.onConnection<FilesResult>(any(), any(), any(), any(), any()))
                .thenThrow(expectedException)

            try {
                dataSource.getFilesResult(server, sharedPath, "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<FilesResult>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
            verifyNoInteractions(fileManager)
        }

    @Test
    fun `when unknown error while retrieving files and directories should return the expected data`() =
        runTest {
            val expectedException = SMBException.UnknownError
            whenever(smbHelper.onConnection<FilesResult>(any(), any(), any(), any(), any()))
                .thenThrow(expectedException)

            try {
                dataSource.getFilesResult(server, sharedPath, "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<FilesResult>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
            verifyNoInteractions(fileManager)
        }

    @Test
    fun `when downloading file should invoke the expected function`() = runTest {
        dataSource.downloadFile(server, sharedPath, "", "", "", "", "")

        verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when exception while downloading file should delete the local file`() = runTest {
        val expectedException = Exception()
        doThrow(expectedException).whenever(smbHelper)
            .onConnection<Unit>(any(), any(), any(), any(), any())
        val file: File = mock {
            on { exists() } doReturn true
            on { delete() } doReturn true
        }
        whenever(fileManager.getLocalFile("", "")).thenReturn(file)

        try {
            dataSource.downloadFile(server, sharedPath, "", "", "", "", "")
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verify(fileManager).getLocalFile("", "")
        verifyNoMoreInteractions(fileManager)
        verify(file).exists()
        verify(file).delete()
        verifyNoMoreInteractions(file)
    }

    @Test
    fun `when connection error while downloading file should return the exception`() = runTest {
        val expectedException = SMBException.ConnectionError
        doThrow(expectedException).whenever(smbHelper)
            .onConnection<Unit>(any(), any(), any(), any(), any())
        val file: File = mock {
            on { exists() } doReturn false
        }
        whenever(fileManager.getLocalFile("", "")).thenReturn(file)

        try {
            dataSource.downloadFile(server, sharedPath, "", "", "", "", "")
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verify(fileManager).getLocalFile("", "")
        verifyNoMoreInteractions(fileManager)
    }

    @Test
    fun `when access error while downloading file should return the exception`() = runTest {
        val expectedException = SMBException.AccessDenied
        doThrow(expectedException).whenever(smbHelper)
            .onConnection<Unit>(any(), any(), any(), any(), any())
        val file: File = mock {
            on { exists() } doReturn false
        }
        whenever(fileManager.getLocalFile("", "")).thenReturn(file)

        try {
            dataSource.downloadFile(server, sharedPath, "", "", "", "", "")
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verify(fileManager).getLocalFile("", "")
        verifyNoMoreInteractions(fileManager)
    }

    @Test
    fun `when cancellation error while downloading file should return the exception`() =
        runTest {
            val expectedException = SMBException.CancelException
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Unit>(any(), any(), any(), any(), any())
            val file: File = mock {
                on { exists() } doReturn false
            }
            whenever(fileManager.getLocalFile("", "")).thenReturn(file)

            try {
                dataSource.downloadFile(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
            verify(fileManager).getLocalFile("", "")
            verifyNoMoreInteractions(fileManager)
        }

    @Test
    fun `when unknown error while downloading file should return the exception`() = runTest {
        val expectedException = SMBException.UnknownError
        doThrow(expectedException).whenever(smbHelper)
            .onConnection<Unit>(any(), any(), any(), any(), any())
        val file: File = mock {
            on { exists() } doReturn false
        }
        whenever(fileManager.getLocalFile("", "")).thenReturn(file)

        try {
            dataSource.downloadFile(server, sharedPath, "", "", "", "", "")
            assert(false)
        } catch (exception: Exception) {
            assert(exception == expectedException)
        }

        verify(smbHelper).onConnection<Unit>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verify(fileManager).getLocalFile("", "")
        verifyNoMoreInteractions(fileManager)
    }

    @Test
    fun `when local file is valid should return true`() = runTest {
        whenever(smbHelper.onConnection<Boolean>(any(), any(), any(), any(), any()))
            .thenReturn(true)

        val isValid = dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")

        assert(isValid)
        verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when local file is not valid should return false`() = runTest {
        whenever(smbHelper.onConnection<Boolean>(any(), any(), any(), any(), any()))
            .thenReturn(false)

        val isValid = dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")

        assert(!isValid)
        verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(smbHelper)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when exception while checking if local file is valid should throw the expected error`() =
        runTest {
            val expectedException = Exception()
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Boolean>(any(), any(), any(), any(), any())

            try {
                dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
        }

    @Test
    fun `when connection error while checking if local file is valid should throw the expected error`() =
        runTest {
            val expectedException = SMBException.ConnectionError
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Boolean>(any(), any(), any(), any(), any())

            try {
                dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
        }

    @Test
    fun `when access error while checking if local file is valid should throw the expected error`() =
        runTest {
            val expectedException = SMBException.AccessDenied
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Boolean>(any(), any(), any(), any(), any())

            try {
                dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
        }

    @Test
    fun `when cancellation error while checking if local file is valid should throw the expected error`() =
        runTest {
            val expectedException = SMBException.CancelException
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Boolean>(any(), any(), any(), any(), any())

            try {
                dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
        }

    @Test
    fun `when unknown error while checking if local file is valid should throw the expected error`() =
        runTest {
            val expectedException = SMBException.UnknownError
            doThrow(expectedException).whenever(smbHelper)
                .onConnection<Boolean>(any(), any(), any(), any(), any())

            try {
                dataSource.isLocalFileValid(server, sharedPath, "", "", "", "", "")
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbHelper).onConnection<Boolean>(any(), any(), any(), any(), any())
            verifyNoMoreInteractions(smbHelper)
        }

    @Test
    fun `when opening local file should return the expected data`() {
        val expectedResult: File = mock()
        whenever(fileManager.getLocalFile("localPath", "Name")).thenReturn(expectedResult)

        val result = dataSource.openFile("localPath", "Name")

        assert(result == expectedResult)
    }

    @Test
    fun `when deleting local file should return the expected data`() {
        dataSource.deleteLocalFile("localPath", "Name")

        verify(fileManager).deleteFile("localPath", "Name")
    }
}
