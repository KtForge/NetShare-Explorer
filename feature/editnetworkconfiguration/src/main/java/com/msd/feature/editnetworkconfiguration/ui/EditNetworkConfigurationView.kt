package com.msd.feature.editnetworkconfiguration.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.msd.feature.editnetworkconfiguration.R
import com.msd.feature.editnetworkconfiguration.presenter.EditNetworkConfigurationPresenter
import com.msd.feature.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Loaded
import com.msd.feature.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Loading
import com.msd.feature.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Uninitialized
import com.msd.ui.widget.AppCrossfade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNetworkConfigurationView(presenter: EditNetworkConfigurationPresenter) {
    val currentState by presenter.getState().collectAsState(initial = Uninitialized)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_screen_title),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = presenter::onNavigateUp
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up_content_description),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        AppCrossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = currentState
        ) { state ->
            when (state) {
                is Loading -> EditNetworkConfigurationLoadingView()
                is Loaded -> EditNetworkConfigurationLoadedView(state, presenter)
                is Uninitialized -> Unit
            }
        }
    }
}
