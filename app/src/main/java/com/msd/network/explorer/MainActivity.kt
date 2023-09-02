package com.msd.network.explorer

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msd.feature.edit.presenter.EditPresenter
import com.msd.feature.explorer.presenter.ExplorerPresenter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun editViewModelFactory(): EditPresenter.Factory
        fun explorerViewModelFactory(): ExplorerPresenter.Factory
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    @Composable
    fun editPresenter(smbConfigurationId: Int): EditPresenter {
        val factory = EntryPointAccessors.fromActivity(
            LocalContext.current as Activity,
            ViewModelFactoryProvider::class.java
        ).editViewModelFactory()

        return viewModel(
            factory = EditPresenter.provideFactory(
                factory,
                smbConfigurationId
            )
        )
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    @Composable
    fun explorerPresenter(
        smbConfigurationId: Int,
        smbConfigurationName: String
    ): ExplorerPresenter {
        val factory = EntryPointAccessors.fromActivity(
            LocalContext.current as Activity,
            ViewModelFactoryProvider::class.java
        ).explorerViewModelFactory()

        return viewModel(
            factory = ExplorerPresenter.provideFactory(
                factory,
                smbConfigurationId,
                smbConfigurationName
            )
        )
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityView(
                context = this,
                editPresenter = { id ->
                    editPresenter(smbConfigurationId = id)
                },
                explorerPresenter = { id, name ->
                    explorerPresenter(smbConfigurationId = id, smbConfigurationName = name)
                }
            )
        }
    }
}
