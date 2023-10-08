package com.msd.feature.main.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.main.ui.MainLoadingView

@Composable
@Preview
fun MainLoadingPreview() {
    NetworkStorageConfigurationTheme {
        MainLoadingView()
    }
}
