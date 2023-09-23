package com.msd.core.ui.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.Dimensions
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme

@Composable
fun ErrorView(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.sizeM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        ErrorMessage(text)
    }
}

@Composable
fun ErrorMessage(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.padding(Dimensions.sizeXL)
    )
}

@Composable
@Preview
fun ErrorPreview() {
    NetworkStorageConfigurationTheme {
        ErrorView(text = "Error message")
    }
}