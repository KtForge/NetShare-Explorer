package com.msd.explorer.presenter

import com.msd.explorer.GetFilesAndDirectoriesUseCase
import com.msd.explorer.OpenFileUseCase
import com.msd.explorer.model.NetworkDirectory
import com.msd.explorer.model.NetworkFile
import com.msd.explorer.model.NetworkParentDirectory
import com.msd.explorer.model.SMBException
import com.msd.explorer.presenter.ExplorerState.Error
import com.msd.explorer.presenter.ExplorerState.Loaded
import com.msd.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
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
    private val parentDirectory = NetworkParentDirectory("Parent", "")
    private val directory = NetworkDirectory("Directory", "")
    private val file = NetworkFile("File", "")
    private val filesAndDirectories = listOf(parentDirectory, directory, file)

    private val expectedPathAndRoot = "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"
    private val loaded = Loaded(
        smbConfiguration,
        root = expectedPathAndRoot,
        path = expectedPathAndRoot,
        filesOrDirectories = emptyList()
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
            verify(core).tryEmit(loaded)
        }
    }

    @Test
    fun `when initializing with invalid id should navigate back`() = runTest {
        smbConfigurationId = -1

        presenter.initialize()
        advanceUntilIdle()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verifyNoInteractions(getFilesAndDirectoriesUseCase)
        inOrder(core) {
            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).navigate(NavigateBack)
        }
    }

    @Test
    fun `when initializing with valid id with connection error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(
                getFilesAndDirectoriesUseCase(
                    smbConfiguration.server,
                    smbConfiguration.sharedPath,
                    "",
                    smbConfiguration.user,
                    smbConfiguration.psw
                )
            ).thenThrow(SMBException.ConnectionError)

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
                verify(core).tryEmit(Error.ConnectionError(smbConfigurationName))
            }
        }

    @Test
    fun `when initializing with valid id with access error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(
                getFilesAndDirectoriesUseCase(
                    smbConfiguration.server,
                    smbConfiguration.sharedPath,
                    "",
                    smbConfiguration.user,
                    smbConfiguration.psw
                )
            ).thenThrow(SMBException.AccessDenied)

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
                verify(core).tryEmit(Error.AccessError(smbConfigurationName))
            }
        }

    @Test
    fun `when initializing with valid id with unknown error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(
                getFilesAndDirectoriesUseCase(
                    smbConfiguration.server,
                    smbConfiguration.sharedPath,
                    "",
                    smbConfiguration.user,
                    smbConfiguration.psw
                )
            ).thenThrow(SMBException.UnknownError)

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
                verify(core).tryEmit(Error.UnknownError(smbConfigurationName))
            }
        }

    @Test
    fun `when initializing with valid id and not found configuration should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(null)

            presenter.initialize()
            advanceUntilIdle()

            verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
            verifyNoInteractions(getFilesAndDirectoriesUseCase)
            inOrder(core) {
                verify(core).tryEmit(Loading(smbConfigurationName))
                verify(core).tryEmit(Error.UnknownError(smbConfigurationName))
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

    @Test
    fun `when clicking on a parent directory in loaded state should navigate back`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)

        presenter.onItemClicked(parentDirectory)
    }

    @Test
    fun `when clicking on back arrow should navigate up`() {
        presenter.onNavigateUp()

        verify(core).navigate(NavigateUp)
    }
}
