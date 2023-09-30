package com.msd.feature.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.annotations.ExcludeFromJacocoGeneratedReport
import com.msd.core.ui.theme.Dimensions.sizeXL
import com.msd.core.ui.theme.Dimensions.sizeXXL
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.smb.model.SMBConfiguration
import com.msd.feature.main.R
import com.msd.feature.main.presenter.UserInteractions

@Composable
fun MainEmptyView(userInteractions: UserInteractions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(sizeXL)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.empty_network_configurations),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sizeXL)
                    .align(Alignment.CenterHorizontally)
            )
        }

        OutlinedButton(
            onClick = userInteractions::onAddButtonClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = sizeXXL)
        ) {
            Text(text = stringResource(id = R.string.add_network_configuration_button))
        }
    }
}

@ExcludeFromJacocoGeneratedReport
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