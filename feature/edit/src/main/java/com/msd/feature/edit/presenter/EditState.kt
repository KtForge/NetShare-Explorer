package com.msd.feature.edit.presenter

import androidx.annotation.StringRes
import com.msd.presentation.State
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

    override fun isUninitialized(): Boolean = this != Uninitialized
    override fun initialState(): State = Uninitialized
}
