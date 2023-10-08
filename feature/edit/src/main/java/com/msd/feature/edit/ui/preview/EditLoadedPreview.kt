package com.msd.feature.edit.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.edit.R
import com.msd.feature.edit.presenter.EditState
import com.msd.feature.edit.presenter.UserInteractions
import com.msd.feature.edit.ui.EditLoadedView

@Composable
@Preview
fun EditLoadedEditConfigurationPreview() {
    val loaded = EditState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = null,
            name = "Configuration",
            server = "Server",
            sharedPath = "Public",
            user = "User",
            psw = "Psw",
        ),
        isPasswordVisible = false,
        actionButtonLabel = R.string.edit_configuration_button,
        serverError = false,
        sharedPathError = false,
    )

    NetworkStorageConfigurationTheme {
        EditLoadedView(loaded, userInteractions = object : UserInteractions {
            override fun onNavigateUp() = Unit
            override fun onPasswordVisibilityIconClicked() = Unit
            override fun onConfirmButtonClicked(
                name: String,
                server: String,
                sharedPath: String,
                user: String,
                psw: String
            ) = Unit
        })
    }
}

@Composable
@Preview
fun EditLoadedEditConfigurationPasswordVisiblePreview() {
    val loaded = EditState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = null,
            name = "Configuration",
            server = "Server",
            sharedPath = "Public",
            user = "User",
            psw = "Psw",
        ),
        isPasswordVisible = true,
        actionButtonLabel = R.string.edit_configuration_button,
        serverError = false,
        sharedPathError = false,
    )

    NetworkStorageConfigurationTheme {
        EditLoadedView(loaded, userInteractions = object : UserInteractions {
            override fun onNavigateUp() = Unit
            override fun onPasswordVisibilityIconClicked() = Unit
            override fun onConfirmButtonClicked(
                name: String,
                server: String,
                sharedPath: String,
                user: String,
                psw: String
            ) = Unit
        })
    }
}

@Composable
@Preview
fun EditLoadedNewConfigurationPreview() {
    val loaded = EditState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = null,
            name = "",
            server = "",
            sharedPath = "",
            user = "",
            psw = "",
        ),
        isPasswordVisible = false,
        actionButtonLabel = R.string.save_configuration_button,
        serverError = false,
        sharedPathError = false,
    )

    NetworkStorageConfigurationTheme {
        EditLoadedView(loaded, userInteractions = object : UserInteractions {
            override fun onNavigateUp() = Unit
            override fun onPasswordVisibilityIconClicked() = Unit
            override fun onConfirmButtonClicked(
                name: String,
                server: String,
                sharedPath: String,
                user: String,
                psw: String
            ) = Unit
        })
    }
}

@Composable
@Preview
fun EditLoadedNewConfigurationErrorsPreview() {
    val loaded = EditState.Loaded(
        smbConfiguration = SMBConfiguration(
            id = null,
            name = "",
            server = "",
            sharedPath = "",
            user = "",
            psw = "",
        ),
        isPasswordVisible = false,
        actionButtonLabel = R.string.save_configuration_button,
        serverError = true,
        sharedPathError = true,
    )

    NetworkStorageConfigurationTheme {
        EditLoadedView(loaded, userInteractions = object : UserInteractions {
            override fun onNavigateUp() = Unit
            override fun onPasswordVisibilityIconClicked() = Unit
            override fun onConfirmButtonClicked(
                name: String,
                server: String,
                sharedPath: String,
                user: String,
                psw: String
            ) = Unit
        })
    }
}
