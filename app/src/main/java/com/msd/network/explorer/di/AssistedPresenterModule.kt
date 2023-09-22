package com.msd.network.explorer.di

import com.msd.feature.edit.presenter.EditState
import com.msd.feature.explorer.presenter.ExplorerState
import com.msd.presentation.IPresenterCore
import com.msd.presentation.PresenterCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AssistedPresenterModule {

    @Provides
    fun provideEditPresenterCore(): IPresenterCore<EditState> {
        return PresenterCore(EditState.Uninitialized)
    }

    @Provides
    fun provideExplorerPresenterCore(): IPresenterCore<ExplorerState> {
        return PresenterCore(ExplorerState.Uninitialized(name = "", path = ""))
    }
}
