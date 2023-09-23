package com.msd.feature.explorer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.msd.core.ui.widget.AppCrossfade
import com.msd.feature.explorer.presenter.ExplorerPresenter
import com.msd.feature.explorer.presenter.ExplorerState.Error
import com.msd.feature.explorer.presenter.ExplorerState.Loaded
import com.msd.feature.explorer.presenter.ExplorerState.Loading
import com.msd.feature.explorer.presenter.ExplorerState.Uninitialized

@Composable
fun ExplorerView(presenter: ExplorerPresenter) {
    val currentState =
        presenter.getState().collectAsState(initial = Uninitialized(name = "", path = "")).value

    Scaffold(
        topBar = { ExplorerTopBar(currentState, userInteractions = presenter) }
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
