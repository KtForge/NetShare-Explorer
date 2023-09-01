package com.msd.feature.edit.presenter

import com.msd.domain.smb.GetSMBConfigurationUseCase
import com.msd.domain.smb.StoreSMBConfigurationUseCase
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.edit.R
import com.msd.feature.edit.presenter.EditState.Loaded
import com.msd.feature.edit.presenter.EditState.Loading
import com.msd.feature.edit.presenter.EditState.Uninitialized
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.presentation.IPresenterCore
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EditPresenterTest : CoroutineTest() {

    private val core: IPresenterCore<EditState> = mock {
        on { currentState() } doReturn Uninitialized
    }
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase = mock()
    private val storeSMBConfigurationUseCase: StoreSMBConfigurationUseCase = mock()
    private var smbConfigurationId = 0
    private val presenter by lazy {
        EditPresenter(
            core,
            getSMBConfigurationUseCase,
            storeSMBConfigurationUseCase,
            smbConfigurationId,
        )
    }

    private val smbConfiguration = SMBConfiguration(
        id = 0,
        name = "Name",
        server = "Server",
        sharedPath = "SharedPath",
        user = "User",
        psw = "Psw",
    )
    private val loaded = Loaded(
        smbConfiguration,
        actionButtonLabel = R.string.edit_configuration_button,
        serverError = false,
        sharedPathError = false
    )

    @Test
    fun `when initializing with valid id should emit the loaded state`() = runTest {
        whenever(getSMBConfigurationUseCase.invoke(smbConfigurationId)).thenReturn(smbConfiguration)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
        verify(core).tryEmit(Loading)
        verify(core).tryEmit(loaded)
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when initializing with no id should emit the loaded state`() {
        smbConfigurationId = -1
        val emptySmbConfiguration = SMBConfiguration(
            id = null,
            name = "",
            server = "",
            sharedPath = "",
            user = "",
            psw = "",
        )

        presenter.initialize()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verify(core).tryEmit(Loading)
        verify(core).tryEmit(
            Loaded(
                emptySmbConfiguration,
                actionButtonLabel = R.string.save_configuration_button,
                serverError = false,
                sharedPathError = false
            )
        )
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when initializing with invalid id should navigate back`() = runTest {
        whenever(getSMBConfigurationUseCase.invoke(smbConfigurationId)).thenReturn(null)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
        verify(core).tryEmit(Loading)
        verify(core).navigate(NavigateBack)
    }

    @Test
    fun `when already initialized should do nothing`() {
        whenever(core.isInitialized()).thenReturn(true)

        presenter.initialize()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when clicking on back arrow should navigate up`() {
        presenter.onNavigateUp()

        verify(core, times(0)).tryEmit(any())
        verify(core).navigate(NavigateUp)
    }

    @Test
    fun `when confirming valid changes in loaded state should save and navigate back`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)

        presenter.onConfirmButtonClicked(
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Password",
        )
        advanceUntilIdle()

        verify(storeSMBConfigurationUseCase).invoke(
            id = smbConfigurationId,
            name = "Name",
            server = "Server",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Password",
        )
        verify(core).tryEmit(Loading)
        verify(core).navigate(NavigateBack)
    }

    @Test
    fun `when confirming invalid server in loaded state should update the state`() {
        whenever(core.currentState()).thenReturn(loaded)
        val expectedState = loaded.copy(
            smbConfiguration = loaded.smbConfiguration.copy(
                name = "Name",
                server = "",
                sharedPath = "SharedPath",
                user = "User",
                psw = "Password",
            ),
            serverError = true
        )

        presenter.onConfirmButtonClicked(
            name = "Name",
            server = "",
            sharedPath = "SharedPath",
            user = "User",
            psw = "Password",
        )

        verifyNoInteractions(storeSMBConfigurationUseCase)
        verify(core).tryEmit(Loading)
        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when confirming invalid sharedPath in loaded state should update the state`() {
        whenever(core.currentState()).thenReturn(loaded)
        val expectedState = loaded.copy(
            smbConfiguration = loaded.smbConfiguration.copy(
                name = "Name",
                server = "Server",
                sharedPath = "",
                user = "User",
                psw = "Password",
            ),
            sharedPathError = true
        )

        presenter.onConfirmButtonClicked(
            name = "Name",
            server = "Server",
            sharedPath = "",
            user = "User",
            psw = "Password",
        )

        verifyNoInteractions(storeSMBConfigurationUseCase)
        verify(core).tryEmit(Loading)
        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when confirming invalid server and sharedPath in loaded state should update the state`() {
        whenever(core.currentState()).thenReturn(loaded)
        val expectedState = loaded.copy(
            smbConfiguration = loaded.smbConfiguration.copy(
                name = "Name",
                server = "",
                sharedPath = "",
                user = "User",
                psw = "Password"
            ),
            serverError = true,
            sharedPathError = true,
        )

        presenter.onConfirmButtonClicked(
            name = "Name",
            server = "",
            sharedPath = "",
            user = "User",
            psw = "Password",
        )

        verifyNoInteractions(storeSMBConfigurationUseCase)
        verify(core).tryEmit(Loading)
        verify(core).tryEmit(expectedState)
    }

    @Test
    fun `when confirming without optional fields in loaded state should save and navigate back`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)

            presenter.onConfirmButtonClicked(
                name = "",
                server = "Server",
                sharedPath = "SharedPath",
                user = "",
                psw = "",
            )
            advanceUntilIdle()

            verify(storeSMBConfigurationUseCase).invoke(
                id = smbConfigurationId,
                name = "",
                server = "Server",
                sharedPath = "SharedPath",
                user = "",
                psw = "",
            )
            verify(core).tryEmit(Loading)
            verify(core).navigate(NavigateBack)
        }
}
