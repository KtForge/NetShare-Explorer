package com.msd.explorer.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.sp
import com.msd.explorer.R
import com.msd.explorer.presenter.ExplorerPresenter
import com.msd.explorer.presenter.ExplorerState.Error
import com.msd.explorer.presenter.ExplorerState.Loaded
import com.msd.explorer.presenter.ExplorerState.Loading
import com.msd.explorer.presenter.ExplorerState.Uninitialized
import com.msd.explorer.presenter.initialState
import com.msd.ui.widget.AppCrossfade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerView(presenter: ExplorerPresenter) {
    val currentState by presenter.getState().collectAsState(initial = initialState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentState.name,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        if (currentState is Loaded) {
                            Text(
                                text = (currentState as Loaded).path,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
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
                is Loading -> ExplorerLoadingView()
                is Loaded -> ExplorerLoadedView(loaded = state, presenter)
                is Error -> ExplorerErrorView(error = state)
                is Uninitialized -> Unit
            }
        }
    }
}