package com.msd.feature.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.msd.feature.main.R

@Composable
fun MainLoadingView() {
    val loadingContentDescription = stringResource(id = R.string.main_loading_indicator_a11y)

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = loadingContentDescription
            }
        )
    }
}
