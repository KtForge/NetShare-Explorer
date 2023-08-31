package com.msd.presentation

import com.msd.navigation.Idle
import com.msd.navigation.NavigateUp
import com.msd.presentation.PresenterCoreTest.TestState.State1
import com.msd.presentation.PresenterCoreTest.TestState.State2
import com.msd.unittest.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PresenterCoreTest : CoroutineTest() {

    sealed interface TestState : State {
        object State1 : TestState
        object State2 : TestState

        override fun isUninitialized(): Boolean = this == State1
        override fun initialState(): State = State1
    }

    private val core = PresenterCore<TestState>(State1)

    @Test
    fun `when getting state should return the state`() = runTest {
        val result = core.state()

        assert(result.first() == State1)
    }

    @Test
    fun `when getting the current state should return the state`() {
        val result = core.currentState()

        assert(result == State1)
    }

    @Test
    fun `when initial state should return false`() {
        val result = core.isInitialized()

        assert(!result)
    }

    @Test
    fun `when not initial state should return true`() {
        core.tryEmit(State2)

        val result = core.isInitialized()

        assert(result)
    }

    @Test
    fun `when emitting a state should emit the state`() {
        core.tryEmit(State2)

        val result = core.currentState()

        assert(result == State2)
    }

    @Test
    fun `when getting the navigation should return idle navigation`() = runTest {
        val result = core.navigation()

        assert(result.first() == Idle)
    }

    @Test
    fun `when navigating should navigate`() = runTest {
        core.navigate(NavigateUp)
        val result = core.navigation()

        assert(result.first() == NavigateUp)
    }
}