package com.msd.feature.explorer.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.sp
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.core.ui.widget.AppTopBar
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.feature.explorer.R
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.feature.explorer.presenter.UserInteractions

@Composable
fun ExplorerTopBar(currentState: ExplorerState, userInteractions: UserInteractions) {
    AppTopBar(
        titleContent = {
            Column {
                Text(
                    text = currentState.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    text = currentState.path,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
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

@Composable
@Preview
fun ExplorerTopBarPreview() {
    NetworkStorageConfigurationTheme {
        ExplorerTopBar(
            currentState = ExplorerState.Loading("Name", "Path"),
            userInteractions = object : UserInteractions {
                override fun onItemClicked(file: IBaseFile) = Unit
                override fun onParentDirectoryClicked(parentDirectory: ParentDirectory) = Unit
                override fun onBackPressed() = Unit
                override fun onNavigateUp() = Unit
                override fun confirmFileAccessErrorDialog() = Unit
                override fun dismissFileAccessErrorDialog() = Unit
                override fun dismissProgressDialog() = Unit
                override fun downloadFile(file: NetworkFile) = Unit
                override fun deleteFile(file: NetworkFile) = Unit

            }
        )
    }
}