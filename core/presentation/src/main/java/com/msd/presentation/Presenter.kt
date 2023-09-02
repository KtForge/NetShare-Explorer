package com.msd.presentation

import androidx.lifecycle.ViewModel
import com.msd.navigation.Idle
import com.msd.navigation.NavigationEvent
import kotlinx.coroutines.flow.Flow

abstract class Presenter<S : State>(private val core: IPresenterCore<S>) : ViewModel() {

    fun getState(): Flow<S> = core.state()

    val currentState: State
        get() = core.currentState()

    fun tryEmit(nextState: S) {
        core.tryEmit(nextState)
    }

    fun getNavigation(): Flow<NavigationEvent> = core.navigation()

    fun navigate(event: NavigationEvent) {
        core.navigate(event)
    }

    fun isInitialized(): Boolean = core.isInitialized()

    open fun initialize() = Unit

    fun cleanNavigation() {
        core.navigate(Idle)
    }
}
