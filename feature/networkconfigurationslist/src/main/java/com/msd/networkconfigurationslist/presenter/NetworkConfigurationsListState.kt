package com.msd.networkconfigurationslist.presenter

import com.msd.presentation.State
import com.msd.smb.model.SMBConfiguration

sealed interface NetworkConfigurationsListState : State {

    object Uninitialized : NetworkConfigurationsListState
    object Empty : NetworkConfigurationsListState
    data class Loaded(
        val smbConfigurations: List<SMBConfiguration>,
        val smbConfigurationItemIdToDelete: Int?,
    ) : NetworkConfigurationsListState

    override fun isUninitialized(): Boolean = this != Uninitialized
}

val initialState = NetworkConfigurationsListState.Uninitialized