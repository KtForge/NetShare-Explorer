package com.msd.feature.main.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.main.ui.MainTopBar

@Composable
@Preview
fun MainTopBarPreview() {
    NetworkStorageConfigurationTheme {
        MainTopBar()
    }
}
