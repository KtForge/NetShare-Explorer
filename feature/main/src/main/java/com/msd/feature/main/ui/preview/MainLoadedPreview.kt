package com.msd.feature.main.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.presenter.MainState
import com.msd.feature.main.presenter.UserInteractions
import com.msd.feature.main.ui.MainLoadedView

@Composable
@Preview
fun MainLoadedPreview() {
    val loaded = MainState.Loaded(
        smbConfigurations = listOf(
            SMBConfiguration(
                id = null,
                name = "Configuration 1",
                server = "Server 1",
                sharedPath = "Public",
                user = "",
                psw = "",
            ),
            SMBConfiguration(
                id = null,
                name = "Configuration 2",
                server = "Server 2",
                sharedPath = "Public",
                user = "",
                psw = "",
            )
        ),
        smbConfigurationItemIdToDelete = null,
    )

    NetworkStorageConfigurationTheme {
        MainLoadedView(
            loaded,
            userInteractions = object : UserInteractions {
                override fun onAddButtonClicked() = Unit
                override fun onNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun onEditNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun onDeleteNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun confirmDeleteDialog() = Unit
                override fun dismissDeleteDialog() = Unit
            })
    }
}

@Composable
@Preview
fun MainLoadedDeleteDialogPreview() {
    val loaded = MainState.Loaded(
        smbConfigurations = listOf(
            SMBConfiguration(
                id = null,
                name = "Configuration 1",
                server = "Server 1",
                sharedPath = "Public",
                user = "",
                psw = "",
            ),
            SMBConfiguration(
                id = null,
                name = "Configuration 2",
                server = "Server 2",
                sharedPath = "Public",
                user = "",
                psw = "",
            )
        ),
        smbConfigurationItemIdToDelete = 0,
    )

    NetworkStorageConfigurationTheme {
        MainLoadedView(
            loaded,
            userInteractions = object : UserInteractions {
                override fun onAddButtonClicked() = Unit
                override fun onNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun onEditNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun onDeleteNetworkConfigurationItemClicked(smbConfiguration: SMBConfiguration) =
                    Unit

                override fun confirmDeleteDialog() = Unit
                override fun dismissDeleteDialog() = Unit
            })
    }
}
