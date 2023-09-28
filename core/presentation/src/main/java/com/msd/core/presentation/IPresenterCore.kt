package com.msd.core.presentation

import com.msd.core.navigation.NavigationEvent
import kotlinx.coroutines.flow.Flow

interface IPresenterCore<S: State> {

    fun state(): Flow<S>
    fun currentState(): S
    fun isInitialized(): Boolean
    fun tryEmit(s: S)
    fun navigation(): Flow<NavigationEvent>
    fun navigate(event: NavigationEvent)
}
