package com.msd.explorer.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.msd.explorer.R
import com.msd.explorer.presenter.ExplorerState.Error
import com.msd.ui.theme.Dimensions.sizeM
import com.msd.ui.theme.Dimensions.sizeXL

@Composable
fun ExplorerErrorView(error: Error) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(sizeM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        when (error) {
            is Error.ConnectionError -> ConnectionErrorMessage()
            is Error.UnknownError -> ConnectionErrorMessage()
        }
    }
}

@Composable
fun ConnectionErrorMessage() {
    Text(
        text = stringResource(id = R.string.connection_error_message),
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.padding(sizeXL)
    )
}
