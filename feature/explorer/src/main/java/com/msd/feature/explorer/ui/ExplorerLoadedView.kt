package com.msd.feature.explorer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.msd.core.ui.theme.Dimensions.sizeL
import com.msd.core.ui.theme.Dimensions.sizeS
import com.msd.core.ui.theme.Dimensions.sizeXL
import com.msd.core.ui.theme.Dimensions.sizeXXL
import com.msd.core.ui.theme.Dimensions.sizeXXXL
import com.msd.domain.explorer.model.NetworkDirectory
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.feature.explorer.R
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.UserInteractions

@Composable
fun ExplorerLoadedView(loaded: Loaded, userInteractions: UserInteractions) {
    BackHandler { userInteractions.onBackPressed() }

    if (loaded.fileAccessError != null) {
        FileAccessErrorDialog(loaded.fileAccessError, userInteractions)
    }

    if (loaded.isDownloadingFile) {
        DownloadProgressDialog(userInteractions)
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = sizeS)
        ) {
            if (loaded.parentDirectory != null) {
                item {
                    ParentDirectoryView(parentDirectory = loaded.parentDirectory, userInteractions)
                }
            }
            loaded.filesOrDirectories.forEach { file ->
                item {
                    when (file) {
                        is NetworkDirectory -> DirectoryView(directory = file, userInteractions)
                        is NetworkFile -> FileView(file, userInteractions)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentDirectoryView(
    parentDirectory: ParentDirectory,
    userInteractions: UserInteractions
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(sizeS)
            .clickable { userInteractions.onParentDirectoryClicked(parentDirectory) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeXXL)
        ) {
            Icon(
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = sizeXL)
                    .size(sizeXXXL)
            )
            Text(
                text = parentDirectory.name,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DirectoryView(directory: NetworkDirectory, userInteractions: UserInteractions) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(sizeS)
            .clickable { userInteractions.onItemClicked(directory) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeXXL)
        ) {
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
}

@Composable
private fun FileView(file: NetworkFile, userInteractions: UserInteractions) {
    var showItemMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(sizeS)
            .clickable { userInteractions.onItemClicked(file) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = sizeXXL)
        ) {
            Icon(
                imageVector = Icons.Outlined.FilePresent,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = sizeXL)
                    .size(sizeXXXL)
            )
            Text(
                text = file.name,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            if (file.isLocal) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Downloaded",
                    modifier = Modifier
                        .padding(horizontal = sizeL)
                        .size(sizeXXXL)
                )
            }
            Column {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More options",
                    modifier = Modifier
                        .padding(end = sizeXL)
                        .size(sizeXXXL)
                        .clip(CircleShape)
                        .clickable { showItemMenu = !showItemMenu }
                )
                DropdownMenu(
                    expanded = showItemMenu,
                    onDismissRequest = { showItemMenu = false }
                ) {
                    if (file.isLocal) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { userInteractions.deleteFile(file) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Download") },
                            onClick = { userInteractions.downloadFile(file) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileAccessErrorDialog(
    error: ExplorerState.Error,
    userInteractions: UserInteractions
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.access_file_error_dialog_title)) },
        text = { Text(text = stringResource(id = error.message)) },
        onDismissRequest = userInteractions::dismissFileAccessErrorDialog,
        confirmButton = {
            TextButton(onClick = userInteractions::confirmFileAccessErrorDialog) {
                Text(stringResource(id = R.string.access_file_error_dialog_edit))
            }
        },
        dismissButton = {
            TextButton(onClick = userInteractions::dismissFileAccessErrorDialog) {
                Text(stringResource(id = R.string.access_file_error_dialog_cancel))
            }
        }
    )
}

@Composable
private fun DownloadProgressDialog(userInteractions: UserInteractions) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.download_progress_dialog_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = sizeXXL)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = userInteractions::dismissProgressDialog) {
                Text(text = stringResource(id = R.string.download_progress_dialog_cancel))
            }
        },
    )
}
