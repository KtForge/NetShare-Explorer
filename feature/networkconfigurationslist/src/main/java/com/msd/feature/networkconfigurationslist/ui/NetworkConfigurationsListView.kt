package com.msd.feature.networkconfigurationslist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.msd.feature.networkconfigurationslist.R
import com.msd.feature.networkconfigurationslist.presenter.NetworkConfigurationsListPresenter
import com.msd.feature.networkconfigurationslist.presenter.NetworkConfigurationsListState.Empty
import com.msd.feature.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loaded
import com.msd.feature.networkconfigurationslist.presenter.NetworkConfigurationsListState.Loading
import com.msd.feature.networkconfigurationslist.presenter.NetworkConfigurationsListState.Uninitialized
import com.msd.ui.widget.AppCrossfade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkConfigurationsListView(presenter: NetworkConfigurationsListPresenter) {
    val currentState by presenter.getState().collectAsState(initial = Uninitialized)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.list_title),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
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
                is Loading -> NetworkConfigurationsListLoadingView()
                is Empty -> NetworkConfigurationsListEmptyView(presenter)
                is Loaded -> NetworkConfigurationsListLoadedView(state, presenter)
                is Uninitialized -> Unit
            }
        }
    }
}
