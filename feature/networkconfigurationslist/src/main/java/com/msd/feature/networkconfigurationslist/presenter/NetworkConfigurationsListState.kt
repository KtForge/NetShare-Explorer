package com.msd.feature.networkconfigurationslist.presenter

import com.msd.presentation.State
import com.msd.domain.smb.model.SMBConfiguration

sealed interface NetworkConfigurationsListState : State {

    object Uninitialized : NetworkConfigurationsListState
    object Loading : NetworkConfigurationsListState
    object Empty : NetworkConfigurationsListState
    data class Loaded(
        val smbConfigurations: List<SMBConfiguration>,
        val smbConfigurationItemIdToDelete: Int?,
    ) : NetworkConfigurationsListState

    override fun isUninitialized(): Boolean = this != Uninitialized
    override fun initialState(): State = Uninitialized
}
