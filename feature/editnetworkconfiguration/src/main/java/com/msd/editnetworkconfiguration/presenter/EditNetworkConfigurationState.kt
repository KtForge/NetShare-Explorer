package com.msd.editnetworkconfiguration.presenter

import androidx.annotation.StringRes
import com.msd.presentation.State
import com.msd.smb.model.SMBConfiguration

sealed interface EditNetworkConfigurationState : State {

    object Uninitialized : EditNetworkConfigurationState
    object Loading : EditNetworkConfigurationState
    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        @StringRes val actionButtonLabel: Int
    ) : EditNetworkConfigurationState

    override fun isUninitialized(): Boolean = this != Uninitialized
}

val initialState = EditNetworkConfigurationState.Uninitialized