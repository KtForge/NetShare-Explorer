package com.msd.feature.edit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.msd.core.ui.theme.Dimensions.sizeM
import com.msd.core.ui.theme.Dimensions.sizeXL
import com.msd.feature.edit.R
import com.msd.feature.edit.presenter.EditState.Loaded
import com.msd.feature.edit.presenter.UserInteractions

@Composable
fun EditLoadedView(loaded: Loaded, userInteractions: UserInteractions) {
    var name by remember { mutableStateOf(loaded.smbConfiguration.name) }
    var server by remember { mutableStateOf(loaded.smbConfiguration.server) }
    var sharedPath by remember { mutableStateOf(loaded.smbConfiguration.sharedPath) }
    var user by remember { mutableStateOf(loaded.smbConfiguration.user) }
    var psw by remember { mutableStateOf(loaded.smbConfiguration.psw) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = sizeM, horizontal = sizeXL)
    ) {
        val nameContentDescription = stringResource(id = R.string.name_field_a11y)
        OutlinedTextField(
            value = name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeM)
                .semantics {
                    contentDescription = nameContentDescription
                },
            onValueChange = { name = it },
            label = {
                Text(text = stringResource(id = R.string.name_label))
            }
        )
        val serverContentDescription = stringResource(id = R.string.server_field_a11y)
        OutlinedTextField(
            value = server,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeM)
                .semantics {
                    contentDescription = serverContentDescription
                },
            onValueChange = { server = it },
            label = {
                Text(text = stringResource(id = R.string.server_label))
            },
            isError = loaded.serverError,
            supportingText = {
                if (loaded.serverError) {
                    Text(text = stringResource(id = R.string.server_field_error))
                }
            },
        )
        val sharedPathContentDescription = stringResource(id = R.string.shared_path_label_a11y)
        OutlinedTextField(
            value = sharedPath,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeM)
                .semantics {
                    contentDescription = sharedPathContentDescription
                },
            onValueChange = { sharedPath = it },
            label = {
                Text(text = stringResource(id = R.string.shared_path_label))
            },
            isError = loaded.sharedPathError,
            supportingText = {
                if (loaded.sharedPathError) {
                    Text(text = stringResource(id = R.string.shared_path_error))
                }
            },
        )
        val userContentDescription = stringResource(id = R.string.user_label_a11y)
        OutlinedTextField(
            value = user,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeM)
                .semantics {
                    contentDescription = userContentDescription
                },
            onValueChange = { user = it },
            label = {
                Text(text = stringResource(id = R.string.user_label))
            }
        )
        val passwordContentDescription = stringResource(id = R.string.password_label_a11y)
        OutlinedTextField(
            value = psw,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeM)
                .semantics {
                    contentDescription = passwordContentDescription
                },
            onValueChange = { psw = it },
            label = {
                Text(text = stringResource(id = R.string.password_label))
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imageVector = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                val contentDescriptionRes = if (passwordVisible) {
                    R.string.hide_password_a11y
                } else {
                    R.string.show_password_a11y
                }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = stringResource(id = contentDescriptionRes)
                    )
                }
            }
        )
        OutlinedButton(
            onClick = {
                userInteractions.onConfirmButtonClicked(name, server, sharedPath, user, psw)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeXL)
        ) {
            Text(text = stringResource(id = loaded.actionButtonLabel))
        }
    }
}
