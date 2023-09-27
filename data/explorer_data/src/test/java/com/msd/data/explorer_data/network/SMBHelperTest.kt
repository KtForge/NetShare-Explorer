package com.msd.data.explorer_data.network

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msdtyp.FileTime
import com.hierynomus.msfscc.fileinformation.FileAllInformation
import com.hierynomus.msfscc.fileinformation.FileBasicInformation
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.Directory
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import com.msd.data.explorer_data.mapper.IFilesAndDirectoriesMapper
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.SMBException
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.InputStream
import java.util.EnumSet

@OptIn(ExperimentalCoroutinesApi::class)
class SMBHelperTest : CoroutineTest() {

    private val server = "Server"
    private val sharedPath = "SharedPath"
    private val user = "User"
    private val psw = "Psw"

    private val diskShare: DiskShare = mock()
    private val session: Session = mock {
        on { connectShare(sharedPath) } doReturn diskShare
    }
    private val connection: Connection = mock {
        on { authenticate(any()) } doReturn session
    }
    private val smbClient: SMBClient = mock {
        on { connect(server) } doReturn connection
    }

    private val fileManager: FileManager = mock()

    private val filesAndDirectoriesMapper: IFilesAndDirectoriesMapper = mock()

    private val helper = SMBHelper(smbClient, filesAndDirectoriesMapper)

    @Test
    fun `when connecting to a remote storage should manage the connection`() = runTest {
        helper.onConnection(server, sharedPath, user, psw) {}

        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
        verifyNoMoreInteractions(session)
        verifyNoMoreInteractions(smbClient)
        verifyNoInteractions(filesAndDirectoriesMapper)
    }

    @Test
    fun `when null while connecting to disk share should throw the exception`() =
        runTest {
            val expectedException = SMBException.UnknownError
            whenever(session.connectShare(sharedPath)).thenReturn(null)

            try {
                helper.onConnection(server, sharedPath, user, psw) {}
                assert(false)
            } catch (exception: Exception) {
                assert(exception == expectedException)
            }

            verify(smbClient).connect(server)
            verify(connection).authenticate(any())
            verify(session).connectShare(sharedPath)
            verify(smbClient).close()
            verifyNoMoreInteractions(session)
            verifyNoMoreInteractions(smbClient)
            verifyNoInteractions(filesAndDirectoriesMapper)
        }

    @Test
    fun `when listing files should return the expected result`() {
        val files: List<FileIdBothDirectoryInformation> = mock()
        val directory: Directory = mock {
            on { list() } doReturn files
        }
        whenever(
            diskShare.openDirectory(
                "",
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        ).thenReturn(directory)
        val expectedResult: FilesResult = mock()
        whenever(
            filesAndDirectoriesMapper.buildFilesResult(
                server,
                sharedPath,
                parentPath = "",
                files,
                fileManager
            )
        ).thenReturn(expectedResult)

        val result = helper.listFiles(server, sharedPath, path = "", diskShare, fileManager)

        assert(result == expectedResult)
        verify(diskShare).openDirectory(
            "",
            EnumSet.of(AccessMask.FILE_READ_DATA),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        verifyNoMoreInteractions(diskShare)
        verify(filesAndDirectoriesMapper).buildFilesResult(
            server,
            sharedPath,
            parentPath = "",
            files,
            fileManager
        )
        verifyNoMoreInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(smbClient)
    }

    @Test
    fun `when getting file size should return the expected result`() {
        val expectedResult = 10L
        val standardInformation: FileStandardInformation = mock {
            on { endOfFile } doReturn expectedResult
        }
        val fileInformation: FileAllInformation = mock {
            on { this.standardInformation } doReturn standardInformation
        }
        val file: File = mock {
            on { this.fileInformation } doReturn fileInformation
        }
        whenever(
            diskShare.openFile(
                "filePath/name",
                EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        ).thenReturn(file)

        val result = helper.getFileSize(diskShare, "filePath", "name")

        assert(result == expectedResult)
        verify(diskShare).openFile(
            "filePath/name",
            EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        verifyNoMoreInteractions(diskShare)
        verify(file).fileInformation
        verifyNoMoreInteractions(file)
        verify(fileInformation).standardInformation
        verifyNoMoreInteractions(fileInformation)
        verify(standardInformation).endOfFile
        verifyNoMoreInteractions(standardInformation)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(smbClient)
    }

    @Test
    fun `when getting file modification time should return the expected result`() {
        val expectedResult = 10L
        val fileTime: FileTime = mock {
            on { toEpochMillis() } doReturn expectedResult
        }
        val basicInformation: FileBasicInformation = mock {
            on { changeTime } doReturn fileTime
        }
        val fileInformation: FileAllInformation = mock {
            on { this.basicInformation } doReturn basicInformation
        }
        val file: File = mock {
            on { this.fileInformation } doReturn fileInformation
        }
        whenever(
            diskShare.openFile(
                "filePath/name",
                EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        ).thenReturn(file)

        val result = helper.getModificationTime(diskShare, "filePath", "name")

        assert(result == expectedResult)
        verify(diskShare).openFile(
            "filePath/name",
            EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        verifyNoMoreInteractions(diskShare)
        verify(file).fileInformation
        verifyNoMoreInteractions(file)
        verify(fileInformation).basicInformation
        verifyNoMoreInteractions(fileInformation)
        verify(basicInformation).changeTime
        verifyNoMoreInteractions(basicInformation)
        verify(fileTime).toEpochMillis()
        verifyNoMoreInteractions(fileTime)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(smbClient)
    }

    @Test
    fun `when getting file input stream should return the expected result`() {
        val expectedResult: InputStream = mock()
        val file: File = mock {
            on { inputStream } doReturn expectedResult
        }
        whenever(
            diskShare.openFile(
                "filePath/name",
                EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            )
        ).thenReturn(file)

        val result = helper.getInputStream(diskShare, "filePath", "name")

        assert(result == expectedResult)
        verify(diskShare).openFile(
            "filePath/name",
            EnumSet.of(AccessMask.MAXIMUM_ALLOWED),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        verifyNoMoreInteractions(diskShare)
        verify(file).inputStream
        verifyNoMoreInteractions(file)
        verifyNoInteractions(filesAndDirectoriesMapper)
        verifyNoInteractions(smbClient)
    }
}
