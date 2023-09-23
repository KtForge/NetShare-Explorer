package com.msd.core.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(titleContent: @Composable () -> Unit, navigationContent: @Composable () -> Unit) {
    TopAppBar(
        title = titleContent,
        navigationIcon = navigationContent,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
@Preview
fun AppTopBarPreview() {
    NetworkStorageConfigurationTheme {
        AppTopBar(
            titleContent = {
                Text(text = "Title")
            },
            navigationContent = {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        )
    }
}