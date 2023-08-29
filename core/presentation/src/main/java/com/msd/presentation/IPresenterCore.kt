package com.msd.presentation

import kotlinx.coroutines.flow.Flow

interface IPresenterCore<S: State> {

    fun state(): Flow<S>
    fun currentState(): S
    fun isInitialized(): Boolean
    fun tryEmit(s: S)
}
