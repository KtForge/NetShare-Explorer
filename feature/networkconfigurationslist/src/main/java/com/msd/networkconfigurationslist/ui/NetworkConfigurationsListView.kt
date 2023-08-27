package com.msd.networkconfigurationslist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListPresenter
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Empty
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loaded
import com.msd.networkconfigurationslist.presenter.NetworkConfigurationsListState.Uninitialized
import com.msd.networkconfigurationslist.presenter.initialState
import com.msd.ui.widget.AppCrossfade

@Composable
fun NetworkConfigurationsListView(presenter: NetworkConfigurationsListPresenter) {
    val currentState by presenter.getState().collectAsState(initial = initialState)

    AppCrossfade(targetState = currentState) { state ->
        when (state) {
            is Empty -> NetworkConfigurationsListEmptyView(presenter)
            is Loaded -> NetworkConfigurationsListLoadedView(state, presenter)
            is Uninitialized -> Unit
        }
    }
}