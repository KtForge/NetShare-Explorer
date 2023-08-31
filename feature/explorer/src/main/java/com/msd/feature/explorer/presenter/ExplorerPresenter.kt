package com.msd.feature.explorer.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.feature.explorer.helper.FilesAndDirectoriesHelper
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.NetworkParentDirectory
import com.msd.domain.explorer.model.SMBException
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNameArg
import com.msd.navigation.OpenFile
import com.msd.presentation.IPresenterCore
import com.msd.presentation.Presenter
import com.msd.domain.smb.GetSMBConfigurationUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ExplorerPresenter @AssistedInject constructor(
    core: IPresenterCore<ExplorerState>,
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase,
    private val filesAndDirectoriesHelper: FilesAndDirectoriesHelper,
    @Assisted(SmbConfigurationRouteIdArg) private val smbConfigurationId: Int,
    @Assisted(SmbConfigurationRouteNameArg) private val smbConfigurationName: String,
) : Presenter<ExplorerState>(core), UserInteractions {

    override fun initialize() {
        if (isInitialized()) return

        tryEmit(Loading(smbConfigurationName))
        viewModelScope.launch {
            if (smbConfigurationId == -1) {
                navigate(NavigateBack)
            } else {
                getSMBConfigurationUseCase(smbConfigurationId)?.let { smbConfiguration ->
                    try {
                        val filesAndDirectories = filesAndDirectoriesHelper.getFilesAndDirectories(
                            smbConfiguration,
                            path = ""
                        )
                        val root = filesAndDirectoriesHelper.getRootPath(smbConfiguration)

                        tryEmit(
                            Loaded(smbConfiguration, root = root, path = root, filesAndDirectories)
                        )
                    } catch (e: Exception) {
                        handleError(e, smbConfigurationName)
                    }
                } ?: tryEmit(Error.UnknownError(smbConfigurationName))
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
            viewModelScope.launch {
                emitFilesAndDirectories(loaded, path = directory.path)
            }
        }
    }

    private fun openFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch {
                tryEmit(Loading(loaded.name))
                val smbConfiguration = loaded.smbConfiguration
                try {
                    tryEmit(loaded)
                    filesAndDirectoriesHelper.openFile(smbConfiguration, file, loaded.path)?.let {
                        navigate(OpenFile(it))
                    }
                } catch (e: Exception) {
                    handleError(e, loaded.name)
                }
            }
        }
    }

    override fun onBackPressed() {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch {
                if (loaded.path == loaded.root) {
                    navigate(NavigateBack)
                } else {
                    val path = loaded.path.substring(0, loaded.path.lastIndexOf("\\"))
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
            handleError(e, loaded.name)
        }
    }

    private fun handleError(e: Exception, name: String) {
        val error = when (e) {
            SMBException.ConnectionError -> Error.ConnectionError(name)
            SMBException.AccessDenied -> Error.AccessError(name)
            else -> Error.UnknownError(name)
        }

        tryEmit(error)
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
}
