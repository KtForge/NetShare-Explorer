package com.msd.feature.edit.tracker

import com.msd.core.tracking.Constants.SMB_CONFIGURATION_CREATED_EVENT
import com.msd.core.tracking.Constants.SMB_CONFIGURATION_EDITED_EVENT
import com.msd.core.tracking.EventsTracker
import javax.inject.Inject

class EditTracker @Inject constructor(private val eventsTracker: EventsTracker) {

    fun logSMBConfigurationCreatedEvent() {
        eventsTracker.logEvent(SMB_CONFIGURATION_CREATED_EVENT)
    }

    fun logSMBConfigurationEditedEvent() {
        eventsTracker.logEvent(SMB_CONFIGURATION_EDITED_EVENT)
    }
}