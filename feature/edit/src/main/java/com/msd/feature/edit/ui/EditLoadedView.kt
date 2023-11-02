package com.msd.feature.edit.ui

import androidx.annotation.StringRes
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = sizeM, horizontal = sizeXL)
    ) {
        TextInputField(
            label = R.string.name_label,
            value = name,
            valueSetter = { name = it },
            isError = false,
            errorText = -1,
            contentDescriptionRes = R.string.name_field_a11y
        )

        TextInputField(
            label = R.string.server_label,
            value = server,
            valueSetter = { server = it },
            isError = loaded.serverError,
            errorText = R.string.server_field_error,
            contentDescriptionRes = R.string.server_field_a11y
        )

        TextInputField(
            label = R.string.shared_path_label,
            value = sharedPath,
            valueSetter = { sharedPath = it },
            isError = loaded.sharedPathError,
            errorText = R.string.shared_path_error,
            contentDescriptionRes = R.string.shared_path_label_a11y
        )

        TextInputField(
            label = R.string.user_label,
            value = user,
            valueSetter = { user = it },
            isError = false,
            errorText = -1,
            contentDescriptionRes = R.string.user_label_a11y
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
            visualTransformation = if (loaded.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imageVector = if (loaded.isPasswordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                val contentDescriptionRes = if (loaded.isPasswordVisible) {
                    R.string.hide_password_a11y
                } else {
                    R.string.show_password_a11y
                }

                IconButton(onClick = { userInteractions.onPasswordVisibilityIconClicked(psw) }) {
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

@Composable
private fun TextInputField(
    @StringRes label: Int,
    value: String,
    valueSetter: (String) -> Unit,
    isError: Boolean,
    @StringRes errorText: Int,
    @StringRes contentDescriptionRes: Int,
) {
    val contentDescription = stringResource(id = contentDescriptionRes)

    OutlinedTextField(
        value = value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = sizeM)
            .semantics {
                this.contentDescription = contentDescription
            },
        onValueChange = valueSetter,
        label = {
            Text(text = stringResource(id = label))
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = stringResource(id = errorText))
            }
        },
    )
}
