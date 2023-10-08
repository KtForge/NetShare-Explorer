package com.msd.feature.main.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msd.core.ui.widget.AppCrossfade
import com.msd.feature.main.presenter.MainPresenter
import com.msd.feature.main.presenter.MainState.Empty
import com.msd.feature.main.presenter.MainState.Loaded
import com.msd.feature.main.presenter.MainState.Loading
import com.msd.feature.main.presenter.MainState.Uninitialized

@Composable
fun MainView(presenter: MainPresenter) {
    val currentState by presenter.getState().collectAsState(initial = Uninitialized)

    Scaffold(topBar = { MainTopBar() }) { paddingValues ->
        AppCrossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = currentState
        ) { state ->
            when (state) {
                is Loading -> MainLoadingView()
                is Empty -> MainEmptyView(presenter)
                is Loaded -> MainLoadedView(state, presenter)
                is Uninitialized -> Unit
            }
        }
    }
}
