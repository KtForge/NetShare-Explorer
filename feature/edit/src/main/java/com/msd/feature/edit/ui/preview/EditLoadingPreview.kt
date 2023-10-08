package com.msd.feature.edit.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.edit.ui.EditLoadingView

@Preview
@Composable
fun EditNetworkConfigurationLoadingPreview() {
    NetworkStorageConfigurationTheme {
        EditLoadingView()
    }
}
