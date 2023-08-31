package com.msd.feature.edit.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.feature.edit.R
import com.msd.feature.edit.presenter.EditState.Loaded
import com.msd.feature.edit.presenter.EditState.Loading
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNoIdArg
import com.msd.presentation.IPresenterCore
import com.msd.presentation.Presenter
import com.msd.domain.smb.GetSMBConfigurationUseCase
import com.msd.domain.smb.StoreSMBConfigurationUseCase
import com.msd.domain.smb.model.SMBConfiguration
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPresenter @AssistedInject constructor(
    core: IPresenterCore<EditState>,
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase,
    private val storeSMBConfigurationUseCase: StoreSMBConfigurationUseCase,
    @Assisted(SmbConfigurationRouteIdArg) val smbConfigurationId: Int,
) : Presenter<EditState>(core), UserInteractions {

    override fun initialize() {
        if (isInitialized()) return

        tryEmit(Loading)
        if (smbConfigurationId.toString() == SmbConfigurationRouteNoIdArg) {
            val smbConfiguration = SMBConfiguration(
                id = null,
                name = "",
                server = "",
                sharedPath = "",
                user = "",
                psw = ""
            )

            tryEmit(
                Loaded(
                    smbConfiguration,
                    actionButtonLabel = R.string.save_configuration_button,
                    serverError = false,
                    sharedPathError = false,
                )
            )
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                getSMBConfigurationUseCase(smbConfigurationId)?.let { smbConfiguration ->
                    tryEmit(
                        Loaded(
                            smbConfiguration,
                            actionButtonLabel = R.string.edit_configuration_button,
                            serverError = false,
                            sharedPathError = false,
                        )
                    )
                } ?: navigate(NavigateBack)
            }
        }
    }

    override fun onNavigateUp() {
        navigate(NavigateUp)
    }

    override fun onConfirmButtonClicked(
        name: String,
        server: String,
        sharedPath: String,
        user: String,
        psw: String
    ) {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(Loading)
            val serverError = validateServer(server)
            val sharedPathError = validateSharedPath(sharedPath)

            if (serverError || sharedPathError) {
                tryEmit(
                    loaded.copy(
                        serverError = serverError,
                        sharedPathError = sharedPathError
                    )
                )
            } else {
                viewModelScope.launch {
                    storeSMBConfigurationUseCase(
                        id = loaded.smbConfiguration.id,
                        name = name,
                        server = server,
                        sharedPath = sharedPath,
                        user = user,
                        psw = psw,
                    )

                    navigate(NavigateBack)
                }
            }
        }
    }

    private fun validateServer(server: String): Boolean = server.isEmpty()

    private fun validateSharedPath(sharedPath: String): Boolean = sharedPath.isEmpty()

    @AssistedFactory
    interface Factory {

        fun create(@Assisted(SmbConfigurationRouteIdArg) smbConfigurationId: Int): EditPresenter
    }

    companion object {

        fun provideFactory(
            assistedFactory: Factory,
            smbConfigurationId: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(smbConfigurationId) as T
            }
        }
    }
}

interface UserInteractions {

    fun onNavigateUp()
    fun onConfirmButtonClicked(
        name: String,
        server: String,
        sharedPath: String,
        user: String,
        psw: String
    )
}
