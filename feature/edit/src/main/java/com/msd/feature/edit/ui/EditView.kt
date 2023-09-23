package com.msd.feature.edit.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msd.core.ui.widget.AppCrossfade
import com.msd.feature.edit.presenter.EditPresenter
import com.msd.feature.edit.presenter.EditState.Loaded
import com.msd.feature.edit.presenter.EditState.Loading
import com.msd.feature.edit.presenter.EditState.Uninitialized

@Composable
fun EditView(presenter: EditPresenter) {
    val currentState by presenter.getState().collectAsState(initial = Uninitialized)

    Scaffold(topBar = { EditTopBar(userInteractions = presenter) }) { paddingValues ->
        AppCrossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = currentState
        ) { state ->
            when (state) {
                is Loading -> EditLoadingView()
                is Loaded -> EditLoadedView(state, presenter)
                is Uninitialized -> Unit
            }
        }
    }
}
