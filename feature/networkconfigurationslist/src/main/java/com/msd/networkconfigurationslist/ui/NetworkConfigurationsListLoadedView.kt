package com.msd.networkconfigurationslist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.msd.networkconfigurationslist.R
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loaded
import com.msd.networkconfigurationslist.presenter.UserInteractions
import com.msd.smb.model.SMBConfiguration
import com.msd.ui.theme.Dimensions.sizeL
import com.msd.ui.theme.Dimensions.sizeM
import com.msd.ui.theme.Dimensions.sizeS
import com.msd.ui.theme.Dimensions.sizeXL

@Composable
fun NetworkConfigurationsListLoadedView(loaded: Loaded, userInteractions: UserInteractions) {
    loaded.smbConfigurationItemIdToDelete?.let {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.delete_dialog_title)) },
            onDismissRequest = userInteractions::dismissDeleteDialog,
            confirmButton = {
                TextButton(onClick = userInteractions::confirmDeleteDialog) {
                    Text(stringResource(id = R.string.delete_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = userInteractions::dismissDeleteDialog) {
                    Text(stringResource(id = R.string.delete_dialog_cancel))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = sizeS)
    ) {
        LazyColumn {
            loaded.smbConfigurations.forEach { smbConfiguration ->
                item {
                    NetworkConfigurationItem(
                        smbConfiguration = smbConfiguration,
                        onOpenNetworkConfigurationItemClicked = userInteractions::onNetworkConfigurationItemClicked,
                        onEditNetworkConfigurationItemClicked = userInteractions::onEditNetworkConfigurationItemClicked,
                        onDeleteNetworkConfigurationItemClicked = userInteractions::onDeleteNetworkConfigurationItemClicked
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(sizeL),
            shape = CircleShape,
            onClick = userInteractions::onAddButtonClicked
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_configuration_button_a11y)
            )
        }
    }
}

@Composable
fun NetworkConfigurationItem(
    smbConfiguration: SMBConfiguration,
    onOpenNetworkConfigurationItemClicked: (SMBConfiguration) -> Unit,
    onEditNetworkConfigurationItemClicked: (SMBConfiguration) -> Unit,
    onDeleteNetworkConfigurationItemClicked: (SMBConfiguration) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = sizeL, vertical = sizeS)
            .clickable { onOpenNetworkConfigurationItemClicked(smbConfiguration) },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(
                top = sizeM,
                start = sizeM,
                end = sizeM,
                bottom = sizeXL
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = smbConfiguration.name,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 24.sp,
                )
                IconButton(onClick = { onEditNetworkConfigurationItemClicked(smbConfiguration) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = stringResource(id = R.string.edit_configuration_button_a11y),
                    )
                }
                IconButton(onClick = { onDeleteNetworkConfigurationItemClicked(smbConfiguration) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        contentDescription = stringResource(id = R.string.delete_configuration_button_a11y),
                    )
                }
            }
            Divider(modifier = Modifier.padding(vertical = sizeS))
            Text(text = stringResource(id = R.string.server_info_label, smbConfiguration.server))
            Text(text = stringResource(id = R.string.path_info_label, smbConfiguration.sharedPath))
        }
    }
}
