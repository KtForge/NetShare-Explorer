package com.msd.feature.explorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.msd.core.ui.widget.LoadingView
import com.msd.feature.explorer.R

@Composable
fun ExplorerLoadingView() {
    LoadingView(contentDescription = stringResource(id = R.string.loading_explorer_a11y))
}
