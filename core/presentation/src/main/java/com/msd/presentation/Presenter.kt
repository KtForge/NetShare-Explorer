package com.msd.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msd.navigation.Idle
import com.msd.navigation.NavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class Presenter<S : State>(private val core: IPresenterCore<S>) : ViewModel() {

    fun getState(): Flow<S> = core.state()

    val currentState: State
        get() = core.currentState()

    fun tryEmit(nextState: S) {
        core.tryEmit(nextState)
    }

    private val navigationEvent = MutableStateFlow<NavigationEvent>(Idle)
    fun getNavigation(): Flow<NavigationEvent> = core.navigation()

    fun navigate(event: NavigationEvent) {
        core.navigate(event)
    }

    fun isInitialized(): Boolean = core.isInitialized()

    open fun initialize() = Unit
    open fun onStart() = Unit
    open fun onResume() = Unit

    fun cleanNavigation() {
        viewModelScope.launch { navigationEvent.tryEmit(Idle) }
    }
}
