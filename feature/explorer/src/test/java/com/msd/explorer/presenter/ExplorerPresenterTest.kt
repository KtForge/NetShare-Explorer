package com.msd.explorer.presenter

import com.msd.explorer.helper.FilesAndDirectoriesHelper
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.NetworkParentDirectory
import com.msd.domain.explorer.model.SMBException
import com.msd.explorer.presenter.ExplorerState.Error
import com.msd.explorer.presenter.ExplorerState.Loaded
import com.msd.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.OpenFile
import com.msd.presentation.IPresenterCore
import com.msd.domain.smb.GetSMBConfigurationUseCase
import com.msd.domain.smb.model.SMBConfiguration
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorerPresenterTest : CoroutineTest() {

    private val core: IPresenterCore<ExplorerState> = mock {
        on { isInitialized() } doReturn false
    }
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase = mock()
    private val filesAndDirectoriesHelper: FilesAndDirectoriesHelper = mock()
    private var smbConfigurationId = 0
    private val smbConfigurationName = "Name"

    private val presenter by lazy {
        ExplorerPresenter(
            core,
            getSMBConfigurationUseCase,
            filesAndDirectoriesHelper,
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
    private val directory = NetworkDirectory("Parent", "path")
    private val file = NetworkFile("Parent", "path")

    private val expectedPathAndRoot =
        "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"
    private val loaded = Loaded(
        smbConfiguration,
        root = expectedPathAndRoot,
        path = expectedPathAndRoot,
        filesOrDirectories = emptyList()
    )

    @Test
    fun `when initializing with valid id successfully should emit Loaded state`() = runTest {
        whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
        whenever(filesAndDirectoriesHelper.getFilesAndDirectories(smbConfiguration, path = ""))
            .thenReturn(emptyList())
        whenever(filesAndDirectoriesHelper.getRootPath(smbConfiguration))
            .thenReturn(expectedPathAndRoot)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(smbConfiguration, path = "")
        verify(filesAndDirectoriesHelper).getRootPath(smbConfiguration)
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
        verifyNoInteractions(filesAndDirectoriesHelper)
        inOrder(core) {
            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).navigate(NavigateBack)
        }
    }

    @Test
    fun `when initializing with valid id with connection error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(filesAndDirectoriesHelper.getFilesAndDirectories(smbConfiguration, path = ""))
                .thenThrow(SMBException.ConnectionError)

            presenter.initialize()
            advanceUntilIdle()

            verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
            verify(filesAndDirectoriesHelper).getFilesAndDirectories(smbConfiguration, path = "")
            inOrder(core) {
                verify(core).tryEmit(Loading(smbConfigurationName))
                verify(core).tryEmit(Error.ConnectionError(smbConfigurationName))
            }
        }

    @Test
    fun `when initializing with valid id with access error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(filesAndDirectoriesHelper.getFilesAndDirectories(smbConfiguration, path = ""))
                .thenThrow(SMBException.AccessDenied)

            presenter.initialize()
            advanceUntilIdle()

            verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
            verify(filesAndDirectoriesHelper).getFilesAndDirectories(smbConfiguration, path = "")
            inOrder(core) {
                verify(core).tryEmit(Loading(smbConfigurationName))
                verify(core).tryEmit(Error.AccessError(smbConfigurationName))
            }
        }

    @Test
    fun `when initializing with valid id with unknown error should emit Error state`() =
        runTest {
            whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
            whenever(filesAndDirectoriesHelper.getFilesAndDirectories(smbConfiguration, path = ""))
                .thenThrow(SMBException.UnknownError)

            presenter.initialize()
            advanceUntilIdle()

            verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
            verify(filesAndDirectoriesHelper).getFilesAndDirectories(smbConfiguration, path = "")
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
            verifyNoInteractions(filesAndDirectoriesHelper)
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
        verifyNoInteractions(filesAndDirectoriesHelper)
        verify(core, times(0)).tryEmit(any())
    }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path should update the state`() =
        runTest {
            val loaded = loaded.copy(path = "$expectedPathAndRoot\\Directory")
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = expectedPathAndRoot
                )
            ).thenReturn(emptyList())
            val expectedState = loaded.copy(path = expectedPathAndRoot)

            presenter.onItemClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(expectedState)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and connection error should emit error state`() =
        runTest {
            val loaded = loaded.copy(path = "$expectedPathAndRoot\\Directory")
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = expectedPathAndRoot
                )
            ).thenThrow(SMBException.ConnectionError)

            presenter.onItemClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.ConnectionError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and access error should emit error state`() =
        runTest {
            val loaded = loaded.copy(path = "$expectedPathAndRoot\\Directory")
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = expectedPathAndRoot
                )
            ).thenThrow(SMBException.AccessDenied)

            presenter.onItemClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.AccessError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and unknown error should emit error state`() =
        runTest {
            val loaded = loaded.copy(path = "$expectedPathAndRoot\\Directory")
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = expectedPathAndRoot
                )
            ).thenThrow(SMBException.UnknownError)

            presenter.onItemClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.UnknownError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and in root path should navigate back`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)

            presenter.onItemClicked(parentDirectory)
            advanceUntilIdle()

            verify(core, times(0)).tryEmit(any())
            verify(core).navigate(NavigateBack)
        }

    @Test
    fun `when clicking on a parent directory not in loaded state should do nothing`() = runTest {
        presenter.onItemClicked(parentDirectory)
        advanceUntilIdle()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when clicking on a directory in loaded state should update the state`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = directory.path
            )
        ).thenReturn(emptyList())
        val expectedState = loaded.copy(path = directory.path)

        presenter.onItemClicked(directory)
        advanceUntilIdle()

        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when clicking on a directory in loaded state and connection error should emit Error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = directory.path
                )
            ).thenThrow(SMBException.ConnectionError)

            presenter.onItemClicked(directory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.ConnectionError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a directory in loaded state and access error should emit Error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = directory.path
                )
            ).thenThrow(SMBException.AccessDenied)

            presenter.onItemClicked(directory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.AccessError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a directory in loaded state and unknown error should emit Error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = directory.path
                )
            ).thenThrow(SMBException.UnknownError)

            presenter.onItemClicked(directory)
            advanceUntilIdle()

            verify(core).tryEmit(Error.UnknownError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a directory not in loaded state should do nothing`() = runTest {
        presenter.onItemClicked(directory)
        advanceUntilIdle()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when clicking on a file in loaded state should update the state and open the file`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            val openedFile: File = mock()
            whenever(
                filesAndDirectoriesHelper.openFile(
                    smbConfiguration,
                    file = file,
                    path = loaded.path
                )
            ).thenReturn(openedFile)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(loaded)
            verify(core).navigate(OpenFile(openedFile))
        }

    @Test
    fun `when clicking on a file in loaded state and null file should restore the state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.openFile(
                    smbConfiguration,
                    file = file,
                    path = loaded.path
                )
            ).thenReturn(null)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(loaded)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and connection error should emit error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.openFile(
                    smbConfiguration,
                    file = file,
                    path = loaded.path
                )
            ).thenThrow(SMBException.ConnectionError)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(Error.ConnectionError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and access error should emit error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.openFile(
                    smbConfiguration,
                    file = file,
                    path = loaded.path
                )
            ).thenThrow(SMBException.AccessDenied)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(Error.AccessError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and unknown error should emit error state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.openFile(
                    smbConfiguration,
                    file = file,
                    path = loaded.path
                )
            ).thenThrow(SMBException.UnknownError)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            verify(core).tryEmit(Loading(smbConfigurationName))
            verify(core).tryEmit(Error.UnknownError(smbConfigurationName))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file not in loaded state should do nothing`() = runTest {
        presenter.onItemClicked(file)
        advanceUntilIdle()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when back pressed in a parent directory in loaded state and in root path should navigate back`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)

            presenter.onBackPressed()
            advanceUntilIdle()

            verify(core, times(0)).tryEmit(any())
            verify(core).navigate(NavigateBack)
        }

    @Test
    fun `when clicking on back arrow should navigate up`() {
        presenter.onNavigateUp()

        verify(core).navigate(NavigateUp)
    }
}
