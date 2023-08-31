package com.msd.smb

import com.msd.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetSMBConfigurationUseCaseTest : CoroutineTest() {

    private val repository: ISMBConfigurationRepository = mock()
    private val useCase = GetSMBConfigurationUseCase(repository)

    private val id = 0

    @Test
    fun `when getting SMB configuration should return the expected value`() = runTest {
        val smbConfiguration: SMBConfiguration = mock()
        whenever(repository.getSmbConfiguration(id)).thenReturn(smbConfiguration)

        val result = useCase(id)

        verify(repository).getSmbConfiguration(id)
        verifyNoMoreInteractions(repository)
        assert(result == smbConfiguration)
    }

    @Test
    fun `when getting SMB configuration returns null should return null`() = runTest {
        whenever(repository.getSmbConfiguration(id)).thenReturn(null)

        val result = useCase(id)

        verify(repository).getSmbConfiguration(id)
        verifyNoMoreInteractions(repository)
        assert(result == null)
    }
}
