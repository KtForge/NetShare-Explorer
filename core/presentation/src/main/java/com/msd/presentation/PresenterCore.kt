package com.msd.presentation

import kotlinx.coroutines.flow.MutableStateFlow

class PresenterCore<S: State>(initialState: S) {

    val state: MutableStateFlow<S> = MutableStateFlow(initialState)

    fun isUninitialized() = state.value.isUninitialized()
}
