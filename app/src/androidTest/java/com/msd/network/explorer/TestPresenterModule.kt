package com.msd.network.explorer

import com.msd.feature.main.presenter.MainState
import com.msd.network.explorer.di.PresenterModule
import com.msd.presentation.IPresenterCore
import com.msd.presentation.PresenterCore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PresenterModule::class]
)
class TestPresenterModule {

    @Singleton
    @Provides
    fun provideMainPresenterCore(): IPresenterCore<MainState> {
        return PresenterCore(MainState.Uninitialized)
    }
}