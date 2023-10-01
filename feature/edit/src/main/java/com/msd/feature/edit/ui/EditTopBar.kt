package com.msd.feature.edit.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.annotations.ExcludeFromJacocoGeneratedReport
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.core.ui.widget.AppTopBar
import com.msd.feature.edit.R
import com.msd.feature.edit.presenter.UserInteractions

@Composable
fun EditTopBar(userInteractions: UserInteractions) {
    AppTopBar(
        titleContent = {
            Text(
                text = stringResource(id = R.string.edit_screen_title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        navigationContent = {
            IconButton(
                onClick = userInteractions::onNavigateUp
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_up_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@ExcludeFromJacocoGeneratedReport
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