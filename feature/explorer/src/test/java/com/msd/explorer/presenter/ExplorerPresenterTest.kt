package com.msd.explorer.presenter

import com.msd.explorer.GetFilesAndDirectoriesUseCase
import com.msd.explorer.OpenFileUseCase
import com.msd.explorer.presenter.ExplorerState.Loaded
import com.msd.explorer.presenter.ExplorerState.Loading
import com.msd.presentation.IPresenterCore
import com.msd.smb.GetSMBConfigurationUseCase
import com.msd.smb.model.SMBConfiguration
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ExplorerPresenterTest : CoroutineTest() {

    private val core: IPresenterCore<ExplorerState> = mock {
        on { isInitialized() } doReturn false
    }
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase = mock()
    private val getFilesAndDirectoriesUseCase: GetFilesAndDirectoriesUseCase = mock()
    private val openFileUseCase: OpenFileUseCase = mock()
    private var smbConfigurationId = 0
    private val smbConfigurationName = "Name"

    private val presenter by lazy {
        ExplorerPresenter(
            core,
            getSMBConfigurationUseCase,
            getFilesAndDirectoriesUseCase,
            openFileUseCase,
            smbConfigurationId,
            smbConfigurationName
        )
    }

    private val smbConfiguration = SMBConfiguration(
        smbConfigurationId,
        smbConfigurationName,
        "Server",
        "SharedPath",
        user = "User",
        psw = "Psw"
    )

    @Test
    fun `when initializing with valid id successfully should emit Loaded state`() = runTest {
        whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
        whenever(
            getFilesAndDirectoriesUseCase(
                smbConfiguration.server,
                smbConfiguration.sharedPath,
                "",
                smbConfiguration.user,
                smbConfiguration.psw
            )
        ).thenReturn(emptyList())
        val expectedPathAndRoot = "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"
        val expectedLoadedState = Loaded(
            smbConfiguration,
            root = expectedPathAndRoot,
            path = expectedPathAndRoot,
            filesOrDirectories = emptyList()
        )

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
        verify(getFilesAndDirectoriesUseCase).invoke(
            smbConfiguration.server,
            smbConfiguration.sharedPath,
            "",
            smbConfiguration.user,
            smbConfiguration.psw
        )
        inOrder(core) {
            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(expectedLoadedState)
        }
    }

    @Test
    fun `when already initialized should do nothing`() = runTest {
        whenever(core.isInitialized()).thenReturn(true)

        presenter.initialize()
        advanceUntilIdle()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        verify(core, times(0)).tryEmit(any())
    }
}