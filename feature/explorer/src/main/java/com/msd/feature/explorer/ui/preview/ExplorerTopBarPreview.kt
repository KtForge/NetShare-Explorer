package com.msd.feature.explorer.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.domain.explorer.model.IBaseFile
import com.msd.domain.explorer.model.NetworkFile
import com.msd.domain.explorer.model.ParentDirectory
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.feature.explorer.presenter.UserInteractions
import com.msd.feature.explorer.ui.ExplorerTopBar

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
