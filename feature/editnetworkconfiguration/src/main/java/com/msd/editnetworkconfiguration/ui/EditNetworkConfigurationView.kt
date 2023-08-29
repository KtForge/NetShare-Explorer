package com.msd.editnetworkconfiguration.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationPresenter
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Loaded
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Loading
import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState.Uninitialized
import com.msd.ui.widget.AppCrossfade

@Composable
fun EditNetworkConfigurationView(presenter: EditNetworkConfigurationPresenter) {
    val currentState by presenter.getState().collectAsState(initial = Uninitialized)

    AppCrossfade(targetState = currentState) { state ->
        when (state) {
            is Loading -> EditNetworkConfigurationLoadingView()
            is Loaded -> EditNetworkConfigurationLoadedView(state, presenter)
            is Uninitialized -> Unit
        }
    }
}
