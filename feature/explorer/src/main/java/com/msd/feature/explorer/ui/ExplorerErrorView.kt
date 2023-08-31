package com.msd.feature.explorer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.core.ui.theme.Dimensions.sizeM
import com.msd.core.ui.theme.Dimensions.sizeXL

@Composable
fun ExplorerErrorView(error: Error) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(sizeM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        ErrorMessage(error.message)
    }
}

@Composable
fun ErrorMessage(@StringRes message: Int) {
    Text(
        text = stringResource(id = message),
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.padding(sizeXL)
    )
}
