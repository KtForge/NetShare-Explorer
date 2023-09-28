package com.msd.data.explorer_data.network

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msdtyp.FileTime
import com.hierynomus.msfscc.fileinformation.FileAllInformation
import com.hierynomus.msfscc.fileinformation.FileBasicInformation
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.Directory
import com.hierynomus.smbj.share.DiskShare
import com.msd.data.explorer_data.mapper.IFilesAndDirectoriesMapper
import com.msd.data.explorer_data.tracker.ExplorerTracker
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.EnumSet

class ExplorerDataSourceIntegrationTest {

    private val server = "Server"
    private val sharedPath = "SharedPath"
    private val user = "User"
    private val psw = "Psw"

    private val path = "path"

    private val directory: Directory = mock {
        on { list() } doReturn emptyList()
    }
    private val standardInformation: FileStandardInformation = mock {
        on { endOfFile } doReturn 10L
    }
    private val fileTime: FileTime = mock {
        on { toEpochMillis() } doReturn 10L
    }
    private val basicInformation: FileBasicInformation = mock {
        on { changeTime } doReturn fileTime
    }
    private val fileInformation: FileAllInformation = mock {
        on { this.basicInformation } doReturn basicInformation
        on { this.standardInformation } doReturn standardInformation
    }
    private val inputStream: InputStream = mock {
        on { read(any()) } doReturn -1
    }
    private val remoteFile: com.hierynomus.smbj.share.File = mock {
        on { this.fileInformation } doReturn fileInformation
        on { this.inputStream } doReturn inputStream
    }
    private val diskShare: DiskShare = mock {
        on {
            openDirectory(
                path,
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        } doReturn directory

        on {
            openFile(
                "path/Name",
                EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        } doReturn remoteFile
    }
    private val session: Session = mock {
        on { connectShare(sharedPath) } doReturn diskShare
    }
    private val connection: Connection = mock {
        on { authenticate(any()) } doReturn session
    }
    private val smbClient: SMBClient = mock {
        on { connect(server) } doReturn connection
    }

    private val filesResult: FilesResult = mock {
        on { filesAndDirectories } doReturn emptyList()
    }
    private val filesAndDirectoriesMapper: IFilesAndDirectoriesMapper = mock {
        on { buildFilesResult(any(), any(), any(), any(), any()) } doReturn filesResult
    }

    private val smbHelper = SMBHelper(smbClient, filesAndDirectoriesMapper)
    private val localFile: File = mock()
    private val outputStream: FileOutputStream = mock()
    private val fileManager: FileManager = mock {
        on { getLocalFile("localPath", "Name") } doReturn localFile
        on { getOutputStream(localFile) } doReturn outputStream
        on { getCreationTimeMillis(localFile) } doReturn 15L
    }
    private val explorerTracker: ExplorerTracker = mock()

    private val dataSource = ExplorerDataSource(smbHelper, fileManager, explorerTracker)

    @Test
    fun `when retrieving files and directories should return the expected data`() = runTest {
        val result = dataSource.getFilesResult(server, sharedPath, path, user, psw)

        assert(result == filesResult)
        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
        verifyNoMoreInteractions(session)
        verifyNoMoreInteractions(smbClient)
        verify(filesAndDirectoriesMapper).buildFilesResult(any(), any(), any(), any(), any())
        verifyNoMoreInteractions(filesAndDirectoriesMapper)
        verify(explorerTracker).logListFilesAndDirectoriesEvent(any(), any())
        verifyNoMoreInteractions(explorerTracker)
    }

    @Test
    fun `when downloading file should invoke the expected classes`() = runTest {
        dataSource.downloadFile(server, sharedPath, "Name", "path", "localPath", user, psw)

        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
        verifyNoMoreInteractions(session)
        verifyNoMoreInteractions(smbClient)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verify(explorerTracker).logDownloadFile(any(), any())
        verifyNoMoreInteractions(explorerTracker)
    }

    @Test
    fun `when local file is valid should return the expected data`() = runTest {
        whenever(localFile.exists()).thenReturn(true)

        val isValid = dataSource.isLocalFileValid(server, sharedPath, "Name", "path", "localPath", user, psw)

        assert(isValid)
        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
        verifyNoMoreInteractions(session)
        verifyNoMoreInteractions(smbClient)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(explorerTracker)
    }

    @Test
    fun `when local file is not valid should return the expected data`() = runTest {
        whenever(localFile.exists()).thenReturn(false)

        val isValid = dataSource.isLocalFileValid(server, sharedPath, "Name", "path", "localPath", user, psw)

        assert(!isValid)
        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
        verifyNoMoreInteractions(session)
        verifyNoMoreInteractions(smbClient)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(explorerTracker)
    }
}