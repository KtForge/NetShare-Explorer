package com.msd.feature.main.tracker

import com.msd.core.tracking.Constants.ADD_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.CONFIGURATION_DELETED_EVENT
import com.msd.core.tracking.Constants.DELETE_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.EDIT_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.OPEN_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.EventsTracker
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class MainTrackerTest {

    private val eventsTracker: EventsTracker = mock()

    private val tracker = MainTracker(eventsTracker)

    @Test
    fun `when tracking open configuration clicked should invoke the events tracker`() {
        tracker.logOpenConfigurationClickedEvent()

        verify(eventsTracker).logEvent(OPEN_CONFIGURATION_CLICKED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }

    @Test
    fun `when tracking edit configuration clicked should invoke the events tracker`() {
        tracker.logEditConfigurationClickedEvent()

        verify(eventsTracker).logEvent(EDIT_CONFIGURATION_CLICKED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }

    @Test
    fun `when tracking add configuration clicked should invoke the events tracker`() {
        tracker.logAddConfigurationClickedEvent()

        verify(eventsTracker).logEvent(ADD_CONFIGURATION_CLICKED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }

    @Test
    fun `when tracking delete configuration clicked should invoke the events tracker`() {
        tracker.logOnDeleteConfigurationClickedEvent()

        verify(eventsTracker).logEvent(DELETE_CONFIGURATION_CLICKED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }

    @Test
    fun `when tracking delete configuration should invoke the events tracker`() {
        tracker.logConfigurationDeletedEvent()

        verify(eventsTracker).logEvent(CONFIGURATION_DELETED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }
}