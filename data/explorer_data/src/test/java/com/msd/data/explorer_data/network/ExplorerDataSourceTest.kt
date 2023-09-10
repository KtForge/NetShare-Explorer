package com.msd.data.explorer_data.network

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.common.SmbPath
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.Directory
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.domain.explorer.model.NetworkFile
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.EnumSet

class ExplorerDataSourceTest {

    private val applicationContext: Context = mock()
    private val client: SMBClient = mock()
    private val explorerTracker: ExplorerTracker = mock()
    private val dataSource = ExplorerDataSource(applicationContext, client, explorerTracker)

    private val server = "192.168.1.1"
    private val sharedPath = "Public"
    private val connection: Connection = mock()
    private val session: Session = mock()
    private val smbPath: SmbPath = mock()
    private val diskShare: DiskShare = mock {
        on { smbPath } doReturn smbPath
    }
    private val directory: Directory = mock()
    private val smbFile: FileIdBothDirectoryInformation = mock()
    private val smbFiles = listOf(smbFile)
    private val file: File = mock()

    @Test
    fun `when retrieving files and directories should return the expected data`() {
        val expectedResult = listOf(NetworkFile("file", "file"))
        whenever(client.connect(server)).thenReturn(connection)
        whenever(connection.authenticate(any())).thenReturn(session)
        whenever(session.connectShare(sharedPath)).thenReturn(diskShare)
        whenever(
            diskShare.openDirectory(
                "",
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null,
            )
        ).thenReturn(directory)
        whenever(
            diskShare.openFile(
                "\\file",
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null,
            )
        ).thenReturn(file)
        whenever(smbPath.toUncPath()).thenReturn("\\\\$server\\$sharedPath")
        whenever(directory.list()).thenReturn(smbFiles)
        whenever(smbFile.fileName).thenReturn("file")
        whenever(smbFile.fileAttributes).thenReturn(FileAttributes.FILE_ATTRIBUTE_NORMAL.value)
        whenever(file.uncPath).thenReturn("file")

        val result = dataSource.getFilesAndDirectories(server, sharedPath, "", "", "")

        assert(result == expectedResult)
        verify(client).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(diskShare).smbPath
        verify(smbPath).toUncPath()
        verify(diskShare).openDirectory(
            "",
            EnumSet.of(AccessMask.FILE_READ_DATA),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null,
        )
        verify(directory).list()
    }
}