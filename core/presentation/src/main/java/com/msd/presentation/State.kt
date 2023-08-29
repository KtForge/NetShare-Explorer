package com.msd.presentation

interface State {

    fun isUninitialized(): Boolean
    fun initialState(): State
}
