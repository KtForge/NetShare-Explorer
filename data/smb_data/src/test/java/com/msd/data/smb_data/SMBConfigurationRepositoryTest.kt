package com.msd.data.smb_data

import android.content.Context
import com.msd.data.files.FileManager
import com.msd.data.smb_data.local.SMBConfigurationDao
import com.msd.data.smb_data.model.DataSMBConfiguration
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SMBConfigurationRepositoryTest : CoroutineTest() {

    private val context: Context = mock()
    private val smbConfigurationDao: SMBConfigurationDao = mock()
    private val fileManager: FileManager = mock()

    private val repository = SMBConfigurationRepository(context, smbConfigurationDao, fileManager)

    @Test
    fun `when getting all configurations should invoke the dao object`() = runTest {
        val dataSMBConfigurations = listOf(
            DataSMBConfiguration(
                id = 0,
                name = "Name",
                server = "Server",
                sharedPath = "SharedPath",
                user = "User",
                psw = "Psw",
            )
        )
        val expectedResult = listOf(
            SMBConfiguration(
                id = 0,
                name = "Name",
                server = "Server",
                sharedPath = "SharedPath",
                user = "User",
                psw = "Psw",
            )
        )
        whenever(smbConfigurationDao.getAll()).thenReturn(flowOf(dataSMBConfigurations))

        val result = repository.getAll()

        assert(result.first() == expectedResult)
        verify(smbConfigurationDao).getAll()
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when getting configuration should invoke the dao object`() = runTest {
        val dataSMBConfiguration = DataSMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )
        val expectedResult = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )
        whenever(smbConfigurationDao.get(0)).thenReturn(dataSMBConfiguration)

        val result = repository.getSmbConfiguration(0)

        assert(result == expectedResult)
        verify(smbConfigurationDao).get(0)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when getting null configuration should invoke the dao object`() = runTest {
        whenever(smbConfigurationDao.get(0)).thenReturn(null)

        val result = repository.getSmbConfiguration(0)

        assert(result == null)
        verify(smbConfigurationDao).get(0)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when inserting configuration should invoke the dao object`() = runTest {
        val dataSMBConfiguration = DataSMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )
        val smbConfiguration = SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )

        repository.insert(smbConfiguration)

        verify(smbConfigurationDao).insert(dataSMBConfiguration)
        verifyNoInteractions(fileManager)
    }

    @Test
    fun `when deleting configuration should invoke the dao object`() = runTest {
        val smbConfiguration = DataSMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )
        whenever(smbConfigurationDao.get(0)).thenReturn(smbConfiguration)

        repository.delete(0)

        verify(smbConfigurationDao).get(0)
        verify(smbConfigurationDao).delete(0)
        verifyNoMoreInteractions(smbConfigurationDao)
        verify(fileManager).deleteServerContents("Server", "SharedPath")
        verifyNoMoreInteractions(fileManager)
    }
}