package com.msd.feature.explorer.presenter

import com.msd.domain.explorer.model.FilesResult
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.SMBException
import com.msd.domain.explorer.model.WorkingDirectory
import com.msd.domain.smb.GetSMBConfigurationUseCase
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.explorer.helper.FilesAndDirectoriesHelper
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.Navigate
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants
import com.msd.navigation.OpenFile
import com.msd.presentation.IPresenterCore
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
import org.mockito.kotlin.verifyNoMoreInteractions
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
            mainCoroutineRule.dispatcher,
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
    private val parentDirectory = ParentDirectory("..", "path", "absolutePath")
    private val workingDirectory = WorkingDirectory("path", "absolutePath")
    private val directory = NetworkDirectory("Parent", "path", "absolutePath")
    private val file = NetworkFile("Parent", "path", "localPath", isLocal = false)
    private val filesResult = FilesResult(
        parentDirectory = parentDirectory,
        workingDirectory = workingDirectory,
        filesAndDirectories = listOf(directory, file)
    )

    private val loaded = Loaded(
        smbConfiguration,
        parentDirectory = parentDirectory,
        workingDirectory = workingDirectory,
        path = workingDirectory.absolutePath,
        filesOrDirectories = listOf(directory, file),
        fileAccessError = null,
        isDownloadingFile = false,
    )

    @Test
    fun `when providing factory should return the expected data`() {
        val expectedViewModel: ExplorerPresenter = mock()
        val assistedFactory: ExplorerPresenter.Factory = mock {
            on { create(smbConfigurationId, smbConfigurationName) } doReturn expectedViewModel
        }

        val factory = ExplorerPresenter.provideFactory(
            assistedFactory,
            smbConfigurationId,
            smbConfigurationName
        )
        val viewModel = factory.create(ExplorerPresenter::class.java)

        assert(viewModel == expectedViewModel)
        verifyNoInteractions(core, getSMBConfigurationUseCase, filesAndDirectoriesHelper)
    }

    @Test
    fun `when initializing with valid id successfully should emit Loaded state`() = runTest {
        whenever(core.currentState()).thenReturn(ExplorerState.Uninitialized("", ""))
        whenever(getSMBConfigurationUseCase(smbConfigurationId)).thenReturn(smbConfiguration)
        whenever(filesAndDirectoriesHelper.getFilesAndDirectories(smbConfiguration, path = ""))
            .thenReturn(filesResult)

        presenter.initialize()
        advanceUntilIdle()

        verify(getSMBConfigurationUseCase).invoke(smbConfigurationId)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(smbConfiguration, path = "")
        inOrder(core) {
            verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
            verify(core).tryEmit(loaded)
        }
    }

    @Test
    fun `when initializing with invalid id should navigate back`() {
        smbConfigurationId = -1

        presenter.initialize()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verifyNoInteractions(filesAndDirectoriesHelper)
        inOrder(core) {
            verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
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
                verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
                verify(core).tryEmit(Error.ConnectionError(smbConfigurationName, path = ""))
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
                verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
                verify(core).tryEmit(Error.AccessError(smbConfigurationName, path = ""))
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
                verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
                verify(core).tryEmit(Error.UnknownError(smbConfigurationName, path = ""))
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
                verify(core).tryEmit(Loading(smbConfigurationName, path = ""))
                verify(core).tryEmit(Error.UnknownError(smbConfigurationName, path = ""))
            }
        }

    @Test
    fun `when already initialized should do nothing`() {
        whenever(core.isInitialized()).thenReturn(true)

        presenter.initialize()

        verifyNoInteractions(getSMBConfigurationUseCase)
        verifyNoInteractions(filesAndDirectoriesHelper)
        verify(core, times(0)).tryEmit(any())
    }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path should update the state`() =
        runTest {
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = parentDirectory.path
                )
            ).thenReturn(filesResult)

            presenter.onParentDirectoryClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(loaded)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and connection error should emit error state`() =
        runTest {
            val expectedState =
                Error.ConnectionError(smbConfigurationName, path = parentDirectory.absolutePath)
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = parentDirectory.path
                )
            ).thenThrow(SMBException.ConnectionError)

            presenter.onParentDirectoryClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(expectedState)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and access error should emit error state`() =
        runTest {
            val expectedState =
                Error.AccessError(smbConfigurationName, path = parentDirectory.absolutePath)
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = parentDirectory.path
                )
            ).thenThrow(SMBException.AccessDenied)

            presenter.onParentDirectoryClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(expectedState)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory in loaded state and not in root path and unknown error should emit error state`() =
        runTest {
            val expectedState =
                Error.UnknownError(smbConfigurationName, path = parentDirectory.absolutePath)
            whenever(core.currentState()).thenReturn(loaded)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = parentDirectory.path
                )
            ).thenThrow(SMBException.UnknownError)

            presenter.onParentDirectoryClicked(parentDirectory)
            advanceUntilIdle()

            verify(core).tryEmit(expectedState)
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a parent directory not in loaded state should do nothing`() {
        presenter.onParentDirectoryClicked(parentDirectory)

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
        ).thenReturn(filesResult)

        presenter.onItemClicked(directory)
        advanceUntilIdle()

        verify(core).tryEmit(Loading(smbConfigurationName, loaded.path))
        verify(core).tryEmit(loaded)
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

            verify(core).tryEmit(Loading(smbConfigurationName, loaded.path))
            verify(core)
                .tryEmit(Error.ConnectionError(smbConfigurationName, directory.absolutePath))
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

            verify(core).tryEmit(Loading(smbConfigurationName, loaded.path))
            verify(core).tryEmit(Error.AccessError(smbConfigurationName, directory.absolutePath))
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

            verify(core).tryEmit(Loading(smbConfigurationName, loaded.path))
            verify(core).tryEmit(Error.UnknownError(smbConfigurationName, directory.absolutePath))
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a directory not in loaded state should do nothing`() {
        presenter.onItemClicked(directory)

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when clicking on a file in loaded state should update the state and open the file`() =
        runTest {
            val firstExpectedState = loaded.copy(isDownloadingFile = true)
            val secondExpectedState = loaded
            whenever(core.currentState()).thenReturn(loaded)
            val openedFile: File = mock()
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = workingDirectory.path
                )
            ).thenReturn(filesResult)
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenReturn(openedFile)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(firstExpectedState)
                verify(core).navigate(OpenFile(openedFile))
                verify(core).tryEmit(secondExpectedState)
            }
        }

    @Test
    fun `when clicking on a downloaded file in loaded state should update the state and open the file`() =
        runTest {
            val file = file.copy(isLocal = true)
            val filesAndDirectories = listOf(file)
            val loaded = loaded.copy(filesOrDirectories = filesAndDirectories)
            whenever(core.currentState()).thenReturn(loaded)
            val filesResult = filesResult.copy(filesAndDirectories = filesAndDirectories)
            whenever(
                filesAndDirectoriesHelper.getFilesAndDirectories(
                    smbConfiguration,
                    path = workingDirectory.path
                )
            ).thenReturn(filesResult)
            val openedFile: File = mock()
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenReturn(openedFile)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(loaded)
                verify(core).navigate(OpenFile(openedFile))
                verify(core).tryEmit(loaded)
            }
        }

    @Test
    fun `when clicking on a file in loaded state and null file should restore the state`() =
        runTest {
            val firstExpectedState = loaded.copy(isDownloadingFile = true)
            val secondExpectedState = loaded.copy(
                fileAccessError = Error.UnknownError(smbConfigurationName, loaded.path)
            )
            whenever(core.currentState()).thenReturn(loaded)
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenReturn(null)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(firstExpectedState)
                verify(core).tryEmit(secondExpectedState)
            }
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and connection error should emit error state`() =
        runTest {
            val firstExpectedState = loaded.copy(isDownloadingFile = true)
            val secondExpectedState = loaded.copy(
                fileAccessError = Error.ConnectionError(smbConfigurationName, path = loaded.path)
            )
            whenever(core.currentState()).thenReturn(loaded)
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenThrow(SMBException.ConnectionError)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(firstExpectedState)
                verify(core).tryEmit(secondExpectedState)
            }
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and access error should emit error state`() =
        runTest {
            val firstExpectedState = loaded.copy(isDownloadingFile = true)
            val secondExpectedState = loaded.copy(
                fileAccessError = Error.AccessError(smbConfigurationName, path = loaded.path)
            )
            whenever(core.currentState()).thenReturn(loaded)
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenThrow(SMBException.AccessDenied)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(firstExpectedState)
                verify(core).tryEmit(secondExpectedState)
            }
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file in loaded state and unknown error should emit error state`() =
        runTest {
            val firstExpectedState = loaded.copy(isDownloadingFile = true)
            val secondExpectedState = loaded.copy(
                fileAccessError = Error.UnknownError(smbConfigurationName, path = loaded.path)
            )
            whenever(core.currentState()).thenReturn(loaded)
            whenever(filesAndDirectoriesHelper.openFile(smbConfiguration, file = file))
                .thenThrow(SMBException.UnknownError)

            presenter.onItemClicked(file)
            advanceUntilIdle()

            inOrder(core) {
                verify(core).tryEmit(firstExpectedState)
                verify(core).tryEmit(secondExpectedState)
            }
            verify(core, times(0)).navigate(any())
        }

    @Test
    fun `when clicking on a file not in loaded state should do nothing`() {
        presenter.onItemClicked(file)

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
    }

    @Test
    fun `when back pressed in loaded state and no parent directory should navigate back`() {
        whenever(core.currentState()).thenReturn(loaded.copy(parentDirectory = null))

        presenter.onBackPressed()

        verify(core, times(0)).tryEmit(any())
        verify(core).navigate(NavigateBack)
    }

    @Test
    fun `when back pressed in loaded state and parent directory should retrieve files`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenReturn(filesResult)

        presenter.onBackPressed()
        advanceUntilIdle()

        verify(core).tryEmit(loaded)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when clicking on back arrow should navigate up`() {
        presenter.onNavigateUp()

        verify(core).navigate(NavigateUp)
    }

    @Test
    fun `when downloading a file successfully should update the state`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenReturn(filesResult)

        presenter.downloadFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = true))
        verify(core).tryEmit(loaded)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).downloadFile(smbConfiguration, file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when downloading a file and cancellation exception should update the state`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(filesAndDirectoriesHelper.downloadFile(smbConfiguration, file))
            .thenThrow(SMBException.CancelException)

        presenter.downloadFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = true))
        verify(core).tryEmit(loaded)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).downloadFile(smbConfiguration, file)
        verify(filesAndDirectoriesHelper, times(0)).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when downloading a file and access exception should show the error`() = runTest {
        val expectedState = loaded.copy(
            fileAccessError = Error.AccessError(smbConfigurationName, workingDirectory.absolutePath)
        )
        whenever(core.currentState()).thenReturn(loaded)
        whenever(filesAndDirectoriesHelper.downloadFile(smbConfiguration, file))
            .thenThrow(SMBException.AccessDenied)

        presenter.downloadFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = true))
        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).downloadFile(smbConfiguration, file)
        verify(filesAndDirectoriesHelper, times(0)).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when downloading a file and connection exception should show the error`() = runTest {
        val expectedState = loaded.copy(
            fileAccessError = Error.ConnectionError(
                smbConfigurationName,
                workingDirectory.absolutePath
            )
        )
        whenever(core.currentState()).thenReturn(loaded)
        whenever(filesAndDirectoriesHelper.downloadFile(smbConfiguration, file))
            .thenThrow(SMBException.ConnectionError)

        presenter.downloadFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = true))
        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).downloadFile(smbConfiguration, file)
        verify(filesAndDirectoriesHelper, times(0)).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when downloading a file and unknown exception should show the error`() = runTest {
        val expectedState = loaded.copy(
            fileAccessError = Error.UnknownError(
                smbConfigurationName,
                workingDirectory.absolutePath
            )
        )
        whenever(core.currentState()).thenReturn(loaded)
        whenever(filesAndDirectoriesHelper.downloadFile(smbConfiguration, file))
            .thenThrow(SMBException.UnknownError)

        presenter.downloadFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = true))
        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).downloadFile(smbConfiguration, file)
        verify(filesAndDirectoriesHelper, times(0)).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file should update the state with files and directories`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenReturn(filesResult)

        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(loaded)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).deleteFile(file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file and not in loaded state should do nothing`() = runTest {
        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file with access error should update the state with error`() = runTest {
        val expectedState = Error.AccessError(smbConfigurationName, loaded.path)
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenThrow(SMBException.AccessDenied)

        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).deleteFile(file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file with connection error should update the state with error`() = runTest {
        val expectedState = Error.ConnectionError(smbConfigurationName, loaded.path)
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenThrow(SMBException.ConnectionError)

        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).deleteFile(file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file with unknown error should update the state with error`() = runTest {
        val expectedState = Error.UnknownError(smbConfigurationName, loaded.path)
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenThrow(SMBException.UnknownError)

        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core).tryEmit(expectedState)
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).deleteFile(file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when deleting file with cancellation error should do nothing`() = runTest {
        whenever(core.currentState()).thenReturn(loaded)
        whenever(
            filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = workingDirectory.path
            )
        ).thenThrow(SMBException.CancelException)

        presenter.deleteFile(file)
        advanceUntilIdle()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
        verify(filesAndDirectoriesHelper).deleteFile(file)
        verify(filesAndDirectoriesHelper).getFilesAndDirectories(
            smbConfiguration,
            path = workingDirectory.path
        )
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when dismissing progress dialog should update the state`() {
        whenever(core.currentState()).thenReturn(loaded.copy(isDownloadingFile = true))

        presenter.dismissProgressDialog()

        verify(core).tryEmit(loaded.copy(isDownloadingFile = false))
        verify(core, times(0)).navigate(any())
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when dismissing progress dialog not in loaded state should do nothing`() {
        presenter.dismissProgressDialog()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when confirming file access error dialog should update the state`() {
        val expectedNavigation = Navigate(
            NavigationConstants.EditNetworkConfiguration
                .replace(
                    NavigationConstants.SmbConfigurationRouteIdArgToReplace,
                    smbConfigurationId.toString()
                )
        )
        whenever(core.currentState()).thenReturn(
            loaded.copy(fileAccessError = Error.AccessError(smbConfigurationName, loaded.path))
        )

        presenter.confirmFileAccessErrorDialog()

        verify(core).navigate(expectedNavigation)
        verify(core).tryEmit(loaded.copy(fileAccessError = null))
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when dismissing file access error dialog should update the state`() {
        whenever(core.currentState()).thenReturn(
            loaded.copy(fileAccessError = Error.AccessError(smbConfigurationName, loaded.path))
        )

        presenter.dismissFileAccessErrorDialog()

        verify(core).tryEmit(loaded.copy(fileAccessError = null))
        verify(core, times(0)).navigate(any())
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }

    @Test
    fun `when dismissing file access error dialog not in loaded state should do nothing`() {
        presenter.dismissFileAccessErrorDialog()

        verify(core, times(0)).tryEmit(any())
        verify(core, times(0)).navigate(any())
        verifyNoMoreInteractions(filesAndDirectoriesHelper)
    }
}
