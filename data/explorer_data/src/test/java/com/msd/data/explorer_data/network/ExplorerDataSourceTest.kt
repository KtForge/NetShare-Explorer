package com.msd.data.explorer_data.network

import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkFile
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ExplorerDataSourceTest {

    private val fileManager: FileManager = mock()
    private val smbHelper: SMBHelper = mock()
    private val explorerTracker: ExplorerTracker = mock()
    private val dataSource = ExplorerDataSource(smbHelper, fileManager, explorerTracker)

    private val server = "192.168.1.1"
    private val sharedPath = "Public"

    @Test
    fun `when retrieving files and directories should return the expected data`() {
        val expectedResult = listOf(NetworkFile("file", "file"))
        whenever(smbHelper.onConnection<List<IBaseFile>>(any(), any(), any(), any(), any()))
            .thenReturn(expectedResult)

        val result = dataSource.getFilesAndDirectories(server, sharedPath, "", "", "")

        assert(result == expectedResult)
    }
}