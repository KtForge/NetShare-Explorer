package com.msd.feature.explorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.annotations.ExcludeFromJacocoGeneratedReport
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.core.ui.widget.ErrorView
import com.msd.feature.explorer.presenter.ExplorerState.Error

@Composable
fun ExplorerErrorView(error: Error) {
    ErrorView(text = stringResource(id = error.message))
}

@ExcludeFromJacocoGeneratedReport
@Composable
@Preview
fun ExplorerErrorPreview() {
    NetworkStorageConfigurationTheme {
        ExplorerErrorView(error = Error.ConnectionError("", ""))
    }
}
