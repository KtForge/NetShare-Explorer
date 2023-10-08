package com.msd.feature.explorer.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.explorer.ui.ExplorerLoadingView

@Composable
@Preview
fun ExplorerLoadingPreview() {
    NetworkStorageConfigurationTheme {
        ExplorerLoadingView()
    }
}
