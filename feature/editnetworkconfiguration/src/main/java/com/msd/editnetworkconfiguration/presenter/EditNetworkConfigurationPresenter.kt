package com.msd.editnetworkconfiguration.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msd.editnetworkconfiguration.R
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Loaded
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Uninitialized
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNoIdArg
import com.msd.presentation.Presenter
import com.msd.smb.GetSMBConfigurationUseCase
import com.msd.smb.StoreSMBConfigurationUseCase
import com.msd.smb.model.SMBConfiguration
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditNetworkConfigurationPresenter @AssistedInject constructor(
    private val getSMBConfigurationUseCase: GetSMBConfigurationUseCase,
    private val storeSMBConfigurationUseCase: StoreSMBConfigurationUseCase,
    @Assisted(SmbConfigurationRouteIdArg) val smbConfigurationId: Int,
) : Presenter<EditNetworkConfigurationState>(), UserInteractions {

    override val state: MutableStateFlow<EditNetworkConfigurationState> =
        MutableStateFlow(Uninitialized)

    override fun initialize() {
        super.initialize()

        viewModelScope.launch {
            if (smbConfigurationId.toString() == SmbConfigurationRouteNoIdArg) {
                val smbConfiguration = SMBConfiguration(
                    id = null,
                    name = "",
                    server = "",
                    sharedPath = "",
                    user = "",
                    psw = ""
                )

                state.tryEmit(
                    Loaded(smbConfiguration, actionButtonLabel = R.string.save_configuration_button)
                )
            } else {
                val smbConfiguration = getSMBConfigurationUseCase(smbConfigurationId)

                state.tryEmit(
                    Loaded(smbConfiguration, actionButtonLabel = R.string.edit_configuration_button)
                )
            }
        }
    }

    override fun onConfirmButtonClicked(
        name: String,
        server: String,
        sharedPath: String,
        user: String,
        psw: String
    ) {
        (state.value as? Loaded)?.let { loaded ->
            viewModelScope.launch {
                storeSMBConfigurationUseCase(
                    id = loaded.smbConfiguration.id,
                    name = name,
                    server = server,
                    sharedPath = sharedPath,
                    user = user,
                    psw = psw,
                )
            }
        }
        navigate(NavigateBack)
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted(SmbConfigurationRouteIdArg) smbConfigurationId: Int,
        ): EditNetworkConfigurationPresenter
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
    fun onConfirmButtonClicked(
        name: String,
        server: String,
        sharedPath: String,
        user: String,
        psw: String
    )
}