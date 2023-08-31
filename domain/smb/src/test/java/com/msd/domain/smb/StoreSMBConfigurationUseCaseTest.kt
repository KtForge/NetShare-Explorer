package com.msd.domain.smb

import com.msd.domain.smb.ISMBConfigurationRepository
import com.msd.domain.smb.StoreSMBConfigurationUseCase
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@OptIn(ExperimentalCoroutinesApi::class)
class StoreSMBConfigurationUseCaseTest : CoroutineTest() {

    private val repository: ISMBConfigurationRepository = mock()
    private val useCase = StoreSMBConfigurationUseCase(repository)

    @Test
    fun `when storing a SMB configuration should invoke the repository`() = runTest {
        val smbConfiguration = SMBConfiguration(
            id = null,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )

        useCase(
            id = null,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )

        verify(repository).insert(smbConfiguration)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `when storing a SMB configuration with null name should invoke the repository`() = runTest {
        val smbConfiguration = SMBConfiguration(
            id = null,
            name = "",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )

        useCase(
            id = null,
            name = null,
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw",
        )

        verify(repository).insert(smbConfiguration)
        verifyNoMoreInteractions(repository)
    }
}
