package com.msd.network.explorer.di

import com.msd.editnetworkconfiguration.presenter.EditNetworkConfigurationState
import com.msd.explorer.presenter.ExplorerState
import com.msd.presentation.PresenterCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AssistedPresenterModule {

    @Provides
    fun provideEditNetworkConfigurationPresenterCore(): PresenterCore<EditNetworkConfigurationState> {
        return PresenterCore(EditNetworkConfigurationState.Uninitialized)
    }

    @Provides
    fun provideExplorerPresenterCore(): PresenterCore<ExplorerState> {
        return PresenterCore(ExplorerState.Uninitialized(""))
    }
}
