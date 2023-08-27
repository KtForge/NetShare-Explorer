package com.msd.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msd.navigation.Idle
import com.msd.navigation.NavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class Presenter<S : State> : ViewModel() {

    protected abstract val state: MutableStateFlow<S>
    fun getState(): Flow<S> = state
    private val navigationEvent = MutableStateFlow<NavigationEvent>(Idle)
    fun getNavigationEvent(): Flow<NavigationEvent> = navigationEvent

    fun navigate(route: NavigationEvent) {
        viewModelScope.launch { navigationEvent.emit(route) }
    }

    open fun initialize() {
        if (!state.value.isUninitialized()) return
    }

    open fun onStart() = Unit
    open fun onResume() = Unit

    fun cleanNavigation() {
        viewModelScope.launch { navigationEvent.emit(Idle) }
    }
}