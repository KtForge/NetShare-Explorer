package com.msd.data.explorer_data.network

import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import com.msd.domain.explorer.model.SMBException
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

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

    private val helper = SMBHelper(smbClient)

    @Test
    fun `when connecting to a remote storage should manage the connection`() = runTest {
        helper.onConnection(server, sharedPath, user, psw) {}

        verify(smbClient).connect(server)
        verify(connection).authenticate(any())
        verify(session).connectShare(sharedPath)
        verify(smbClient).close()
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
        }
}