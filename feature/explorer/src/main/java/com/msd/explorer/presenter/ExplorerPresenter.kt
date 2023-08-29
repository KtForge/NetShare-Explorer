package com.msd.explorer.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.explorer.GetFilesAndDirectoriesUseCase
import com.msd.explorer.OpenFileUseCase
import com.msd.explorer.model.IBaseFile
import com.msd.explorer.model.NetworkDirectory
import com.msd.explorer.model.NetworkFile
import com.msd.explorer.model.NetworkParentDirectory
import com.msd.explorer.model.SMBException
import com.msd.explorer.presenter.ExplorerState.Error
import com.msd.explorer.presenter.ExplorerState.Loaded
import com.msd.explorer.presenter.ExplorerState.Loading
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNameArg
import com.msd.navigation.OpenFile
import com.msd.presentation.Presenter
import com.msd.presentation.PresenterCore
import com.msd.smb.GetSMBConfigurationUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ExplorerPresenter @AssistedInject constructor(
    core: PresenterCore<ExplorerState>,
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase,
    private val getFilesAndDirectoriesUseCase: GetFilesAndDirectoriesUseCase,
    private val openFileUseCase: OpenFileUseCase,
    @Assisted(SmbConfigurationRouteIdArg) private val smbConfigurationId: Int,
    @Assisted(SmbConfigurationRouteNameArg) private val smbConfigurationName: String,
) : Presenter<ExplorerState>(core), UserInteractions {

    override fun initialize() {
        super.initialize()

        viewModelScope.launch {
            if (smbConfigurationId == -1) {
                navigate(NavigateBack)
            } else {
                tryEmit(Loading(smbConfigurationName))
                val smbConfiguration = getSMBConfigurationUseCase(smbConfigurationId)
                try {
                    val filesAndDirectories = getFilesAndDirectoriesUseCase(
                        server = smbConfiguration.server,
                        sharedPath = smbConfiguration.sharedPath,
                        directoryRelativePath = "",
                        user = smbConfiguration.user,
                        psw = smbConfiguration.psw
                    )
                    val root = "\\\\${smbConfiguration.server}\\${smbConfiguration.sharedPath}"

                    tryEmit(
                        Loaded(
                            smbConfiguration,
                            root = root,
                            path = root,
                            filesAndDirectories
                        )
                    )
                } catch (e: Exception) {
                    handleError(e, smbConfigurationName)
                }
            }
        }
    }

    override fun onItemClicked(file: IBaseFile) {
        when (file) {
            is NetworkDirectory -> openDirectory(file)
            is NetworkFile -> openFile(file)
            is NetworkParentDirectory -> onBackPressed()
        }
    }

    private fun openDirectory(directory: NetworkDirectory) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch {
                val smbConfiguration = loaded.smbConfiguration
                try {
                    val filesAndDirectories = getFilesAndDirectoriesUseCase(
                        server = smbConfiguration.server,
                        sharedPath = smbConfiguration.sharedPath,
                        directoryRelativePath = directory.path,
                        user = smbConfiguration.user,
                        psw = smbConfiguration.psw
                    )
                    tryEmit(
                        loaded.copy(
                            path = directory.path,
                            filesOrDirectories = filesAndDirectories
                        )
                    )
                } catch (e: Exception) {
                    handleError(e, loaded.name)
                }
            }
        }
    }

    private fun openFile(file: NetworkFile) {
        (currentState as? Loaded)?.let { loaded ->
            viewModelScope.launch {
                tryEmit(Loading(loaded.name))
                val smbConfiguration = loaded.smbConfiguration
                try {
                    openFileUseCase(
                        server = smbConfiguration.server,
                        sharedPath = smbConfiguration.sharedPath,
                        directoryRelativePath = loaded.path,
                        fileName = file.name,
                        user = smbConfiguration.user,
                        psw = smbConfiguration.psw
                    )?.let {
                        tryEmit(loaded)
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
                    val smbConfiguration = loaded.smbConfiguration
                    try {
                        val filesAndDirectories = getFilesAndDirectoriesUseCase(
                            server = smbConfiguration.server,
                            sharedPath = smbConfiguration.sharedPath,
                            directoryRelativePath = path,
                            user = smbConfiguration.user,
                            psw = smbConfiguration.psw
                        )

                        tryEmit(
                            loaded.copy(
                                path = path,
                                filesOrDirectories = filesAndDirectories
                            )
                        )
                    } catch (e: Exception) {
                        handleError(e, loaded.name)
                    }
                }
            }
        }
    }

    private fun handleError(e: Exception, name: String) {
        val error = if (e == SMBException.ConnectionError) {
            Error.ConnectionError(name)
        } else {
            Error.UnknownError(name)
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
