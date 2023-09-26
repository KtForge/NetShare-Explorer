package com.msd.presentation

import com.msd.navigation.Idle
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigationEvent
import com.msd.presentation.PresenterTest.TestState.State1
import com.msd.presentation.PresenterTest.TestState.State2
import kotlinx.coroutines.flow.Flow
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class PresenterTest {

    sealed interface TestState : State {
        object State1 : TestState
        object State2 : TestState
    }
    private val core: IPresenterCore<TestState> = mock()
    private val presenter = object : Presenter<TestState>(core) {}

    @Test
    fun `when emitting state should invoke the core`() {
        presenter.tryEmit(State2)

        verify(core).tryEmit(State2)
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when navigating should invoke the core`() {
        presenter.navigate(NavigateBack)

        verify(core).navigate(NavigateBack)
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when checking for initialization should invoke the core`() {
        presenter.isInitialized()

        verify(core).isInitialized()
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when getting state should invoke the core`() {
        val state: Flow<TestState> = mock()
        whenever(core.state()).thenReturn(state)

        val result = presenter.getState()

        assert(result == state)
        verify(core).state()
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when getting current state should invoke the core`() {
        val currentState = State1
        whenever(core.currentState()).thenReturn(currentState)

        val result = presenter.currentState

        assert(result == currentState)
        verify(core).currentState()
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when getting navigation should invoke the core`() {
        val navigation: Flow<NavigationEvent> = mock()
        whenever(core.navigation()).thenReturn(navigation)

        val result = presenter.getNavigation()

        assert(result == navigation)
        verify(core).navigation()
        verifyNoMoreInteractions(core)
    }

    @Test
    fun `when cleaning navigation should invoke the core`() {
        presenter.cleanNavigation()

        verify(core).navigate(Idle)
        verifyNoMoreInteractions(core)
    }
}