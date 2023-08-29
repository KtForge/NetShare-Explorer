package com.msd.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PresenterCore<S: State>(private val initialState: S): IPresenterCore<S> {

    private val state: MutableStateFlow<S> = MutableStateFlow(initialState)

    override fun state(): Flow<S> = state

    override fun currentState(): S = state.value

    override fun isInitialized(): Boolean = currentState() != initialState

    override fun tryEmit(s: S) {
        state.tryEmit(s)
    }
}
