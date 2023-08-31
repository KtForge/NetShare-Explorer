package com.msd.feature.main.presenter

import androidx.lifecycle.viewModelScope
import com.msd.domain.smb.DeleteSMBConfigurationUseCase
import com.msd.domain.smb.GetSMBConfigurationsUseCase
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.presenter.MainState.Empty
import com.msd.feature.main.presenter.MainState.Loaded
import com.msd.feature.main.presenter.MainState.Loading
import com.msd.navigation.Navigate
import com.msd.navigation.NavigationConstants.EditNetworkConfiguration
import com.msd.navigation.NavigationConstants.Explorer
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArgToReplace
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNameArgToReplace
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNoIdArg
import com.msd.presentation.IPresenterCore
import com.msd.presentation.Presenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkConfigurationsListPresenter @Inject constructor(
    core: IPresenterCore<MainState>,
    private val getSMBConfigurationsUseCase: GetSMBConfigurationsUseCase,
    private val deleteSMBConfigurationUseCase: DeleteSMBConfigurationUseCase,
) : Presenter<MainState>(core), UserInteractions {

    override fun initialize() {
        if (isInitialized()) return

        tryEmit(Loading)
        viewModelScope.launch {
            getSMBConfigurationsUseCase().collect {
                handleSMBConfigurations(it)
            }
        }
    }

    override fun onAddButtonClicked() {
        val route = EditNetworkConfiguration
            .replace(SmbConfigurationRouteIdArgToReplace, SmbConfigurationRouteNoIdArg)

        navigate(Navigate(route))
    }

    override fun onNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) {
        val route = Explorer
            .replace(
                SmbConfigurationRouteIdArgToReplace,
                smbConfiguration.id.toString()
            )
            .replace(
                SmbConfigurationRouteNameArgToReplace,
                smbConfiguration.name
            )

        navigate(Navigate(route))
    }

    override fun onEditNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) {
        val route = EditNetworkConfiguration
            .replace(
                SmbConfigurationRouteIdArgToReplace,
                smbConfiguration.id.toString()
            )

        navigate(Navigate(route))
    }

    override fun onDeleteNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(loaded.copy(smbConfigurationItemIdToDelete = smbConfiguration.id))
        }
    }

    override fun confirmDeleteDialog() {
        (currentState as? Loaded)?.let { loaded ->
            loaded.smbConfigurationItemIdToDelete?.let { id ->
                viewModelScope.launch {
                    deleteSMBConfigurationUseCase(id)
                }
            }
        }
    }

    override fun dismissDeleteDialog() {
        (currentState as? Loaded)?.let { loaded ->
            tryEmit(loaded.copy(smbConfigurationItemIdToDelete = null))
        }
    }

    private fun handleSMBConfigurations(smbConfigurations: List<SMBConfiguration>) {
        if (smbConfigurations.isEmpty()) {
            tryEmit(Empty)
        } else {
            tryEmit(
                Loaded(
                    smbConfigurations,
                    smbConfigurationItemIdToDelete = null
                )
            )
        }
    }
}

interface UserInteractions {
    fun onAddButtonClicked()
    fun onNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration)
    fun onEditNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration)
    fun onDeleteNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration)
    fun confirmDeleteDialog()
    fun dismissDeleteDialog()
}
