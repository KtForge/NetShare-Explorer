package com.msd.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.annotations.ExcludeFromJacocoGeneratedReport
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.core.ui.widget.LoadingView
import com.msd.feature.main.R

@Composable
fun MainLoadingView() {
    LoadingView(contentDescription = stringResource(id = R.string.main_loading_indicator_a11y))
}

@ExcludeFromJacocoGeneratedReport
@Composable
@Preview
fun MainLoadingPreview() {
    NetworkStorageConfigurationTheme {
        MainLoadingView()
    }
}
