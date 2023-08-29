package com.msd.editnetworkconfiguration.presenter

import androidx.annotation.StringRes
import com.msd.presentation.State
import com.msd.smb.model.SMBConfiguration

sealed interface EditNetworkConfigurationState : State {

    object Uninitialized : EditNetworkConfigurationState
    object Loading : EditNetworkConfigurationState
    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        @StringRes val actionButtonLabel: Int,
        val serverError: Boolean,
        val sharedPathError: Boolean,
    ) : EditNetworkConfigurationState

    override fun isUninitialized(): Boolean = this != Uninitialized
    override fun initialState(): State = Uninitialized
}
