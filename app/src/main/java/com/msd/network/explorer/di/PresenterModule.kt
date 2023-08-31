package com.msd.network.explorer.di

import com.msd.feature.main.presenter.MainState
import com.msd.presentation.IPresenterCore
import com.msd.presentation.PresenterCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class PresenterModule {

    @Provides
    fun provideNetworkConfigurationsListPresenterCore(): IPresenterCore<MainState> {
        return PresenterCore(MainState.Uninitialized)
    }
}
