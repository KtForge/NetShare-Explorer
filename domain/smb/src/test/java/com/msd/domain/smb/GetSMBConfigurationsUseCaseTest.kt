package com.msd.domain.smb

import com.msd.domain.smb.GetSMBConfigurationsUseCase
import com.msd.domain.smb.ISMBConfigurationRepository
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class GetSMBConfigurationsUseCaseTest : CoroutineTest() {

    private val smbConfigurationRepository: ISMBConfigurationRepository = mock()
    private val useCase = GetSMBConfigurationsUseCase(smbConfigurationRepository)

    @Test
    fun `when getting the configurations should invoke the repository`() = runTest {
        useCase()

        verify(smbConfigurationRepository).getAll()
    }
}
