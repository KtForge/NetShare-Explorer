package com.msd.feature.main.presenter

import com.msd.presentation.State
import com.msd.domain.smb.model.SMBConfiguration

sealed interface MainState : State {

    object Uninitialized : MainState
    object Loading : MainState
    object Empty : MainState
    data class Loaded(
        val smbConfigurations: List<SMBConfiguration>,
        val smbConfigurationItemIdToDelete: Int?,
    ) : MainState
}
