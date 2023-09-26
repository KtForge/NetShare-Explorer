package com.msd.feature.edit.tracker

import com.msd.core.tracking.Constants.SMB_CONFIGURATION_CREATED_EVENT
import com.msd.core.tracking.Constants.SMB_CONFIGURATION_EDITED_EVENT
import com.msd.core.tracking.EventsTracker
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class EditTrackerTest {

    private val eventsTracker: EventsTracker = mock()

    private val tracker = EditTracker(eventsTracker)

    @Test
    fun `when tracking configuration created should invoke the tracker`() {
        tracker.logSMBConfigurationCreatedEvent()

        verify(eventsTracker).logEvent(SMB_CONFIGURATION_CREATED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }

    @Test
    fun `when tracking configuration edited should invoke the tracker`() {
        tracker.logSMBConfigurationEditedEvent()

        verify(eventsTracker).logEvent(SMB_CONFIGURATION_EDITED_EVENT)
        verifyNoMoreInteractions(eventsTracker)
    }
}
