package com.msd.feature.explorer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.explorer.R

@Composable
fun ExplorerLoadingView() {
    val loadingContentDescription = stringResource(id = R.string.loading_explorer_a11y)

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .semantics {
                    contentDescription = loadingContentDescription
                }
        )
    }
}

@Composable
@Preview
fun ExplorerLoadingPreview() {
    NetworkStorageConfigurationTheme {
        ExplorerLoadingView()
    }
}