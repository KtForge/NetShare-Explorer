package com.msd.data.explorer_data.network

import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
    }
}