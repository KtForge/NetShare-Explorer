package com.msd.feature.edit.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.edit.presenter.UserInteractions
import com.msd.feature.edit.ui.EditTopBar

@Composable
@Preview
fun EditTopBarPreview() {
    NetworkStorageConfigurationTheme {
        EditTopBar(
            userInteractions = object : UserInteractions {
                override fun onNavigateUp() = Unit
                override fun onPasswordVisibilityIconClicked() = Unit
                override fun onConfirmButtonClicked(
                    name: String,
                    server: String,
                    sharedPath: String,
                    user: String,
                    psw: String
                ) = Unit
            }
        )
    }
}
