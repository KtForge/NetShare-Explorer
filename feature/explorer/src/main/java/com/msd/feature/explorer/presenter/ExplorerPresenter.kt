package com.msd.feature.explorer.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.NetworkParentDirectory
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                        val filesAndDirectories =
                            filesAndDirectoriesHelper.getFilesAndDirectories(
                                smbConfiguration,
                                path = ""
                            )
                        val root = filesAndDirectoriesHelper.getRootPath(smbConfiguration)

                        tryEmit(
                            Loaded(
                                smbConfiguration,
                                root = root,
                                path = root,
                                filesOrDirectories = filesAndDirectories,
                                fileAccessError = null,
                                isDownloadingFile = false,
                            )
                        )
                    } catch (e: Exception) {
                        tryEmit(handleError(e, smbConfigurationName, path = ""))
                    }
                } ?: tryEmit(Error.UnknownError(smbConfigurationName, path = ""))
            }
        }
    }

    override fun onItemClicked(file: IBaseFile) {
        when (file) {
            is NetworkParentDirectory -> onBackPressed()
            is NetworkDirectory -> openDirectory(file)
            is NetworkFile -> openFile(file)
        }
    }

    private fun openDirectory(directory: NetworkDirectory) {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(Loading(smbConfigurationName, loaded.path))
            viewModelScope.launch(ioDispatcher) {
                emitFilesAndDirectories(loaded, path = directory.path)
            }
        }
    }

    private fun openFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch(ioDispatcher) {
                val smbConfiguration = loaded.smbConfiguration
                downloadJob = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        tryEmit(loaded.copy(isDownloadingFile = !file.isLocal))

                        val fileToOpen = filesAndDirectoriesHelper.openFile(
                            smbConfiguration,
                            file,
                            loaded.path
                        )

                        navigate(OpenFile(fileToOpen))
                        emitFilesAndDirectories(loaded, loaded.path)
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            tryEmit(
                                loaded.copy(
                                    fileAccessError = handleError(e, loaded.name, loaded.path)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        (currentState as? Loaded)?.let { loaded ->
            if (loaded.path == loaded.root) {
                navigate(NavigateBack)
            } else {
                val path = loaded.path.substring(0, loaded.path.lastIndexOf("\\"))
                viewModelScope.launch(ioDispatcher) {
                    emitFilesAndDirectories(loaded, path)
                }
            }
        }
    }

    private suspend fun emitFilesAndDirectories(loaded: Loaded, path: String) {
        val smbConfiguration = loaded.smbConfiguration
        try {
            val filesAndDirectories = filesAndDirectoriesHelper.getFilesAndDirectories(
                smbConfiguration,
                path = path
            )

            tryEmit(loaded.copy(path = path, filesOrDirectories = filesAndDirectories))
        } catch (e: Exception) {
            tryEmit(handleError(e, loaded.name, path))
        }
    }

    private fun handleError(e: Exception, name: String, path: String): Error {
        return when (e) {
            SMBException.ConnectionError -> Error.ConnectionError(name, path)
            SMBException.AccessDenied -> Error.AccessError(name, path)
            else -> Error.UnknownError(name, path)
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

    }

    override fun deleteFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch(ioDispatcher) {
                filesAndDirectoriesHelper.deleteFile(file)

                emitFilesAndDirectories(loaded, loaded.path)
            }
        }
    }

    override fun onNavigateUp() {
        navigate(NavigateUp)
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
    fun onBackPressed()
    fun onNavigateUp()
    fun confirmDialog()
    fun dismissDialog()
    fun dismissProgressDialog()
    fun downloadFile(file: NetworkFile)
    fun deleteFile(file: NetworkFile)
}
