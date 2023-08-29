package com.msd.networkconfigurationslist.presenter

import com.msd.navigation.Idle
import com.msd.navigation.Navigate
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Empty
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loaded
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Uninitialized
import com.msd.presentation.PresenterCore
import com.msd.smb.DeleteSMBConfigurationUseCase
import com.msd.smb.GetSMBConfigurationsUseCase
import com.msd.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkConfigurationsListPresenterTest : CoroutineTest() {

    private val core: PresenterCore<NetworkConfigurationsListState> = mock()
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

        verify(core)
    }

    @Test
    fun `when initializing without data should emit Empty state`() = runTest {
        whenever(getSMBConfigurationsUseCase.invoke()).thenReturn(emptyList())

        presenter.initialize()

        assert(presenter.getState().first() == Uninitialized)
        assert(presenter.getState().drop(1).first() == Empty)
    }

    @Test
    fun `when add button is clicked should navigate to the edit screen`() = runTest {
        presenter.onAddButtonClicked()

        assert(presenter.getNavigationEvent().first() == Idle)
        assert(presenter.getNavigationEvent().drop(1).first() == Navigate("settings/-1"))
    }

    @Test
    fun `when clicking on a network configuration should navigate to the explorer screen`() =
        runTest {
            presenter.onNetworkConfigurationItemClicked(smbConfigurations.first())

            assert(presenter.getNavigationEvent().first() == Idle)
            assert(presenter.getNavigationEvent().drop(1).first() == Navigate("explorer/0/Name"))
        }

    @Test
    fun `when edit button is clicked should navigate to the edit screen`() = runTest {
        presenter.onEditNetworkConfigurationItemClicked(smbConfigurations.first())

        assert(presenter.getNavigationEvent().first() == Idle)
        assert(presenter.getNavigationEvent().drop(1).first() == Navigate("settings/0"))
    }

    @Test
    fun `when delete button is clicked should update the Loaded state`() = runTest {
        whenever(getSMBConfigurationsUseCase.invoke()).thenReturn(smbConfigurations)
        val expectedState = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)

        presenter.initialize()

        assert(presenter.getState().drop(1).first() == expectedState)

        presenter.onDeleteNetworkConfigurationItemClicked(smbConfigurations.first())

        assert(presenter.getState().drop(1).first() == expectedState.copy(smbConfigurationItemIdToDelete = 0))
    }

    @Test
    fun `when confirming network deletion should update the Loaded state`() = runTest {
        whenever(getSMBConfigurationsUseCase.invoke()).thenReturn(smbConfigurations)
        whenever(deleteSMBConfigurationUseCase.invoke(0)).thenReturn(Unit)
        val expectedState = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)

        presenter.initialize()

        assert(presenter.getState().drop(1).first() == expectedState)

        presenter.confirmDeleteDialog()

        assert(presenter.getState().drop(1).first() == expectedState.copy(smbConfigurationItemIdToDelete = null))
    }
}
