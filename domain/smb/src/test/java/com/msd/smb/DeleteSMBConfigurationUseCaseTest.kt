package com.msd.smb

import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteSMBConfigurationUseCaseTest : CoroutineTest() {

    private val repository: ISMBConfigurationRepository = mock()
    private val useCase = DeleteSMBConfigurationUseCase(repository)

    private val id = 0

    @Test
    fun `when deleting SMB configuration should invoke the repository`() = runTest {
        useCase(id)

        verify(repository).delete(id)
        verifyNoMoreInteractions(repository)
    }
}
