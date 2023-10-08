package com.msd.feature.explorer.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.feature.explorer.ui.ExplorerErrorView

@Composable
@Preview
fun ExplorerErrorPreview() {
    NetworkStorageConfigurationTheme {
        ExplorerErrorView(error = ExplorerState.Error.ConnectionError("", ""))
    }
}
