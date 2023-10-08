package com.msd.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.msd.core.ui.widget.LoadingView
import com.msd.feature.main.R

@Composable
fun MainLoadingView() {
    LoadingView(contentDescription = stringResource(id = R.string.main_loading_indicator_a11y))
}
