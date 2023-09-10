package com.msd.feature.main.presenter

import com.msd.domain.smb.DeleteSMBConfigurationUseCase
import com.msd.domain.smb.GetSMBConfigurationsUseCase
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.presenter.MainState.Empty
import com.msd.feature.main.presenter.MainState.Loaded
import com.msd.feature.main.presenter.MainState.Loading
import com.msd.feature.main.tracker.MainTracker
import com.msd.navigation.Navigate
import com.msd.presentation.IPresenterCore
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainPresenterTest : CoroutineTest() {

    private val core: IPresenterCore<MainState> = mock {
        on { isInitialized() } doReturn false
    }
    private val getSMBConfigurationsUseCase: GetSMBConfigurationsUseCase = mock()
    private val deleteSMBConfigurationUseCase: DeleteSMBConfigurationUseCase = mock()
    private val mainTracker: MainTracker = mock()
    private val presenter = MainPresenter(
        core,
        getSMBConfigurationsUseCase,
        deleteSMBConfigurationUseCase,
        mainTracker
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
        whenever(getSMBConfigurationsUseCase.invoke()).thenReturn(flowOf(smbConfigurations))
        val expectedState = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationsUseCase).invoke()
        inOrder(core) {
            verify(core).tryEmit(Loading)
            verify(core).tryEmit(expectedState)
        }
    }

    @Test
    fun `when already initialized should not fetch configuration`() {
        whenever(core.isInitialized()).thenReturn(true)

        presenter.initialize()

        verifyNoInteractions(getSMBConfigurationsUseCase)
    }

    @Test
    fun `when add button clicked should navigate to the edit screen`() {
        val expectedRoute = "settings/-1"
        val expectedEvent = Navigate(routeId = expectedRoute)

        presenter.onAddButtonClicked()

        verify(core).navigate(expectedEvent)
    }

    @Test
    fun `when item clicked should navigate to the explorer screen`() {
        val expectedRoute = "explorer/0/Name"
        val expectedEvent = Navigate(routeId = expectedRoute)

        presenter.onNetworkConfigurationItemClicked(smbConfigurations.first())

        verify(core).navigate(expectedEvent)
    }

    @Test
    fun `when edit item button clicked should navigate to the edit screen`() {
        val expectedRoute = "settings/0"
        val expectedEvent = Navigate(routeId = expectedRoute)

        presenter.onEditNetworkConfigurationItemClicked(smbConfigurations.first())

        verify(core).navigate(expectedEvent)
    }

    @Test
    fun `when delete item button clicked and in loaded state should update the state`() {
        val state = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)
        whenever(core.currentState()).thenReturn(state)
        val expectedState = state.copy(smbConfigurationItemIdToDelete = 0)

        presenter.onDeleteNetworkConfigurationItemClicked(smbConfigurations.first())

        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when delete item button clicked and not in loaded state should do nothing`() {
        whenever(core.currentState()).thenReturn(Empty)

        presenter.onDeleteNetworkConfigurationItemClicked(smbConfigurations.first())

        verify(core, times(0)).tryEmit(any())
    }

    @Test
    fun `when deleting item in loaded state with id to delete should invoke delete use case`() =
        runTest {
            val state = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = 0)
            whenever(core.currentState()).thenReturn(state)

            presenter.confirmDeleteDialog()
            advanceUntilIdle()

            verify(core, times(0)).tryEmit(any())
            verify(deleteSMBConfigurationUseCase).invoke(0)
        }

    @Test
    fun `when deleting item in loaded state with no id to delete should do nothing`() {
        val state = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = null)
        whenever(core.currentState()).thenReturn(state)

        presenter.confirmDeleteDialog()

        verify(core, times(0)).tryEmit(any())
        verifyNoInteractions(deleteSMBConfigurationUseCase)
    }

    @Test
    fun `when deleting item not in loaded state should do nothing`() {
        whenever(core.currentState()).thenReturn(Empty)

        presenter.confirmDeleteDialog()

        verify(core, times(0)).tryEmit(any())
        verifyNoInteractions(deleteSMBConfigurationUseCase)
    }

    @Test
    fun `when dismissing delete dialog in loaded state should clear the id to delete`() {
        val state = Loaded(smbConfigurations, smbConfigurationItemIdToDelete = 0)
        whenever(core.currentState()).thenReturn(state)
        val expectedState = state.copy(smbConfigurationItemIdToDelete = null)

        presenter.dismissDeleteDialog()

        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when dismissing delete dialog not in loaded state should do nothing`() {
        whenever(core.currentState()).thenReturn(Empty)

        presenter.dismissDeleteDialog()

        verify(core, times(0)).tryEmit(any())
    }
}
