package com.msd.feature.edit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msd.ui.theme.NetworkStorageConfigurationTheme

@Composable
fun EditLoadingView() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun EditNetworkConfigurationLoadingPreview() {
    NetworkStorageConfigurationTheme {
        EditLoadingView()
    }
}