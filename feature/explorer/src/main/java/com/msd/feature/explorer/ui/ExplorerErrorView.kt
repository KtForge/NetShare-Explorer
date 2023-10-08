package com.msd.feature.explorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.msd.core.ui.widget.ErrorView
import com.msd.feature.explorer.presenter.ExplorerState.Error

@Composable
fun ExplorerErrorView(error: Error) {
    ErrorView(text = stringResource(id = error.message))
}
