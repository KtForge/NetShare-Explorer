package com.msd.networkconfigurationslist.presenter

import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loaded
import com.msd.presentation.IPresenterCore
import com.msd.smb.DeleteSMBConfigurationUseCase
import com.msd.smb.GetSMBConfigurationsUseCase
import com.msd.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkConfigurationsListPresenterTest : CoroutineTest() {

    private val core: IPresenterCore<NetworkConfigurationsListState> = mock {
        on { isInitialized() } doReturn false
    }
    private val getSMBConfigurationsUseCase: GetSMBConfigurationsUseCase = mock()
    private val deleteSMBConfigurationUseCase: DeleteSMBConfigurationUseCase = mock()
    private val presenter = NetworkConfigurationsListPresenter(
        core,
        getSMBConfigurationsUseCase,
        deleteSMBConfigurationUseCase
    )

    private val smbConfigurations = listOf(
        SMBConfiguration(
            id = 0,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Psw"
        )
    )

    @Test
    fun `when initializing should emit Loaded state with the expected models`() = runTest {
        whenever(getSMBConfigurationsUseCase.invoke()).thenReturn(smbConfigurations)
        val expectedState = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationsUseCase).invoke()
        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when already initialized should not fetch configuration`() = runTest {
        whenever(core.isInitialized()).thenReturn(true)

        presenter.initialize()
        advanceUntilIdle()

        verifyNoInteractions(getSMBConfigurationsUseCase)
    }
}
