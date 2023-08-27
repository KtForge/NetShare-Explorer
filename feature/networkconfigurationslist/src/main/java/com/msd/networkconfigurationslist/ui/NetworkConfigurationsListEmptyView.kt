package com.msd.networkconfigurationslist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.msd.networkconfigurationslist.R
import com.msd.networkconfigurationslist.presenter.UserInteractions
import com.msd.ui.theme.Dimensions.sizeXL
import com.msd.ui.theme.Dimensions.sizeXXL

@Composable
fun NetworkConfigurationsListEmptyView(userInteractions: UserInteractions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(sizeXL)
    ) {
        Card {
            Text(
                text = stringResource(id = R.string.empty_network_configurations),
                modifier = Modifier.padding(sizeXL)
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