package com.msd.feature.main.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.presenter.UserInteractions
import com.msd.feature.main.ui.MainEmptyView

@Composable
@Preview
fun MainEmptyPreview() {
    NetworkStorageConfigurationTheme {
        MainEmptyView(
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
            }
        )
    }
}
