package com.msd.feature.explorer.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.domain.explorer.model.SMBException
import com.msd.domain.smb.GetSMBConfigurationUseCase
import com.msd.feature.explorer.helper.FilesAndDirectoriesHelper
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.Navigate
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNameArg
import com.msd.navigation.OpenFile
import com.msd.presentation.IPresenterCore
import com.msd.presentation.IoDispatcher
import com.msd.presentation.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExplorerPresenter @AssistedInject constructor(
    core: IPresenterCore<ExplorerState>,
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase,
    private val filesAndDirectoriesHelper: FilesAndDirectoriesHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @Assisted(SmbConfigurationRouteIdArg) private val smbConfigurationId: Int,
    @Assisted(SmbConfigurationRouteNameArg) private val smbConfigurationName: String,
) : Presenter<ExplorerState>(core), UserInteractions {

    private var downloadJob: Job? = null

    override fun initialize() {
        if (isInitialized()) return

        tryEmit(Loading(smbConfigurationName, path = ""))
        if (smbConfigurationId == -1) {
            navigate(NavigateBack)
        } else {
            viewModelScope.launch(ioDispatcher) {
                getSMBConfigurationUseCase(smbConfigurationId)?.let { smbConfiguration ->
                    try {
                        val filesResult = filesAndDirectoriesHelper.getFilesAndDirectories(
                            smbConfiguration,
                            path = ""
                        )

                        tryEmit(
                            Loaded(
                                smbConfiguration,
                                parentDirectory = filesResult.parentDirectory,
                                workingDirectory = filesResult.workingDirectory,
                                path = filesResult.workingDirectory.absolutePath,
                                filesOrDirectories = filesResult.filesAndDirectories,
                                fileAccessError = null,
                                isDownloadingFile = false,
                            )
                        )
                    } catch (e: Exception) {
                        handleError(e, smbConfigurationName, path = "")?.let { tryEmit(it) }
                    }
                } ?: tryEmit(Error.UnknownError(smbConfigurationName, path = ""))
            }
        }
    }

    override fun onItemClicked(file: IBaseFile) {
        when (file) {
            is NetworkDirectory -> openDirectory(file.path, file.absolutePath)
            is NetworkFile -> openFile(file)
        }
    }

    override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) {
        openDirectory(parentDirectory.path, parentDirectory.absolutePath)
    }

    private fun openDirectory(relativePath: String, absolutePath: String) {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(Loading(smbConfigurationName, loaded.path))
            viewModelScope.launch(ioDispatcher) {
                emitFilesAndDirectories(loaded, relativePath, absolutePath)
            }
        }
    }

    private fun openFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            downloadJob = viewModelScope.launch(ioDispatcher) {
                try {
                    tryEmit(loaded.copy(isDownloadingFile = !file.isLocal))

                    val fileToOpen = filesAndDirectoriesHelper.openFile(
                        loaded.smbConfiguration,
                        file,
                    )

                    navigate(OpenFile(fileToOpen))
                    emitFilesAndDirectories(
                        loaded,
                        loaded.workingDirectory.path,
                        loaded.workingDirectory.absolutePath
                    )
                } catch (e: Exception) {
                    tryEmit(loaded.copy(fileAccessError = handleError(e, loaded.name, loaded.path)))
                }
            }
        }
    }

    override fun onBackPressed() {
        (currentState as? Loaded)?.let { loaded ->
            if (loaded.parentDirectory != null) {
                viewModelScope.launch(ioDispatcher) {
                    openDirectory(loaded.parentDirectory.path, loaded.parentDirectory.absolutePath)
                }
            } else {
                navigate(NavigateBack)
            }
        }
    }

    override fun confirmDialog() {
        dismissDialog()

        val route = NavigationConstants.EditNetworkConfiguration
            .replace(
                NavigationConstants.SmbConfigurationRouteIdArgToReplace,
                smbConfigurationId.toString()
            )

        navigate(Navigate(route))
    }

    override fun dismissDialog() {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(loaded.copy(fileAccessError = null))
        }
    }

    override fun dismissProgressDialog() {
        (currentState as? Loaded)?.let { loaded ->
            downloadJob?.cancel()
            tryEmit(loaded.copy(isDownloadingFile = false))
        }
    }

    override fun downloadFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            downloadJob = viewModelScope.launch(ioDispatcher) {
                if (!file.isLocal) {
                    tryEmit(loaded.copy(isDownloadingFile = true))

                    try {
                        filesAndDirectoriesHelper.downloadFile(loaded.smbConfiguration, file)

                        emitFilesAndDirectories(
                            loaded,
                            loaded.workingDirectory.path,
                            loaded.workingDirectory.absolutePath
                        )
                    } catch (e: Exception) {
                        tryEmit(
                            loaded.copy(fileAccessError = handleError(e, loaded.name, loaded.path))
                        )
                    }
                }
            }
        }
    }

    override fun deleteFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch(ioDispatcher) {
                filesAndDirectoriesHelper.deleteFile(file)

                emitFilesAndDirectories(
                    loaded,
                    loaded.workingDirectory.path,
                    loaded.workingDirectory.absolutePath
                )
            }
        }
    }

    private suspend fun emitFilesAndDirectories(
        loaded: Loaded,
        relativePath: String,
        absolutePath: String
    ) {
        val smbConfiguration = loaded.smbConfiguration

        try {
            val filesResult = filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = relativePath
            )

            tryEmit(
                loaded.copy(
                    path = filesResult.workingDirectory.absolutePath,
                    parentDirectory = filesResult.parentDirectory,
                    workingDirectory = filesResult.workingDirectory,
                    filesOrDirectories = filesResult.filesAndDirectories,
                    isDownloadingFile = false,
                )
            )
        } catch (e: Exception) {
            handleError(e, loaded.name, absolutePath)?.let { tryEmit(it) }
        }
    }

    override fun onNavigateUp() {
        navigate(NavigateUp)
    }

    private fun handleError(e: Exception, name: String, path: String): Error? {
        return when (e) {
            SMBException.ConnectionError -> Error.ConnectionError(name, path)
            SMBException.AccessDenied -> Error.AccessError(name, path)
            SMBException.CancelException -> null
            else -> Error.UnknownError(name, path)
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted(SmbConfigurationRouteIdArg) smbConfigurationId: Int,
            @Assisted(SmbConfigurationRouteNameArg) smbConfigurationName: String,
        ): ExplorerPresenter
    }

    companion object {

        fun provideFactory(
            assistedFactory: Factory,
            smbConfigurationId: Int,
            smbConfigurationName: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(smbConfigurationId, smbConfigurationName) as T
            }
        }
    }
}

interface UserInteractions {
    fun onItemClicked(file: IBaseFile)
    fun onParentDirectoryClicked(parentDirectory: ParentDirectory)
    fun onBackPressed()
    fun onNavigateUp()
    fun confirmDialog()
    fun dismissDialog()
    fun dismissProgressDialog()
    fun downloadFile(file: NetworkFile)
    fun deleteFile(file: NetworkFile)
}
