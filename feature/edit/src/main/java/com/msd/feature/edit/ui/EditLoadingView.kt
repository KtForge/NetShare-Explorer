package com.msd.feature.edit.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.annotations.ExcludeFromJacocoGeneratedReport
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.core.ui.widget.LoadingView
import com.msd.feature.edit.R

@Composable
fun EditLoadingView() {
    LoadingView(contentDescription = stringResource(id = R.string.loading_edit_a11y))
}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun EditNetworkConfigurationLoadingPreview() {
    NetworkStorageConfigurationTheme {
        EditLoadingView()
    }
}
