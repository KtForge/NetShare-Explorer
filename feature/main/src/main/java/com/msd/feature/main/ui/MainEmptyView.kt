package com.msd.feature.main.ui

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
import com.msd.feature.main.R
import com.msd.feature.main.presenter.UserInteractions
import com.msd.core.ui.theme.Dimensions.sizeXL
import com.msd.core.ui.theme.Dimensions.sizeXXL

@Composable
fun MainEmptyView(userInteractions: UserInteractions) {
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
