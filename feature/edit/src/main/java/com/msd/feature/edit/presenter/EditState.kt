package com.msd.feature.edit.presenter

import androidx.annotation.StringRes
import com.msd.core.presentation.State
import com.msd.domain.smb.model.SMBConfiguration

sealed interface EditState : State {

    object Uninitialized : EditState
    object Loading : EditState
    data class Loaded(
        val smbConfiguration: SMBConfiguration,
        val isPasswordVisible: Boolean,
        @StringRes val actionButtonLabel: Int,
        val serverError: Boolean,
        val sharedPathError: Boolean,
    ) : EditState
}
