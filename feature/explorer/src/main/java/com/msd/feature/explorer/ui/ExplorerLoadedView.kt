package com.msd.feature.explorer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msd.core.ui.theme.Dimensions.sizeS
import com.msd.core.ui.theme.Dimensions.sizeXL
import com.msd.core.ui.theme.Dimensions.sizeXXL
import com.msd.core.ui.theme.Dimensions.sizeXXXL
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.NetworkParentDirectory
import com.msd.feature.explorer.R
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.UserInteractions

@Composable
fun ExplorerLoadedView(loaded: Loaded, userInteractions: UserInteractions) {
    BackHandler { userInteractions.onBackPressed() }

    loaded.fileAccessError?.let { error ->
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.access_file_error_dialog_title)) },
            text = { Text(text = stringResource(id = error.message)) },
            onDismissRequest = userInteractions::dismissDialog,
            confirmButton = {
                TextButton(onClick = userInteractions::confirmDialog) {
                    Text(stringResource(id = R.string.access_file_error_dialog_edit))
                }
            },
            dismissButton = {
                TextButton(onClick = userInteractions::dismissDialog) {
                    Text(stringResource(id = R.string.access_file_error_dialog_cancel))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = sizeS)
    ) {
        loaded.filesOrDirectories.forEach { file ->
            item {
                val containerColor = if (file is NetworkFile) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(sizeS)
                        .clickable { userInteractions.onItemClicked(file) },
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = sizeXXL)
                    ) {
                        when (file) {
                            is NetworkParentDirectory -> ParentDirectoryView(
                                directory = file,
                                scope = this
                            )

                            is NetworkDirectory -> DirectoryView(directory = file, scope = this)
                            is NetworkFile -> FileView(file = file, scope = this)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentDirectoryView(directory: NetworkParentDirectory, scope: RowScope) {
    with(scope) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = sizeXL)
                .size(sizeXXXL)
        )
        Text(
            text = directory.name,
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun DirectoryView(directory: NetworkDirectory, scope: RowScope) {
    with(scope) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = sizeXL)
                .size(sizeXXXL)
        )
        Text(
            text = directory.name,
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun FileView(file: NetworkFile, scope: RowScope) {
    with(scope) {
        Icon(
            imageVector = Icons.Outlined.FilePresent,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = sizeXL)
                .size(sizeXXXL)
        )
        Text(
            text = file.name,
            modifier = Modifier.align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
        )
    }
}
