package com.msd.presentation

import com.msd.navigation.Idle
import com.msd.navigation.NavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PresenterCore<S : State>(private val initialState: S) : IPresenterCore<S> {

    private val state: MutableStateFlow<S> = MutableStateFlow(initialState)
    private val navigation: MutableStateFlow<NavigationEvent> = MutableStateFlow(Idle)

    override fun state(): Flow<S> = state

    override fun currentState(): S = state.value

    override fun isInitialized(): Boolean = currentState() != initialState

    override fun tryEmit(s: S) {
        state.tryEmit(s)
    }

    override fun navigation(): Flow<NavigationEvent> = navigation

    override fun navigate(event: NavigationEvent) {
        navigation.tryEmit(event)
    }
}
