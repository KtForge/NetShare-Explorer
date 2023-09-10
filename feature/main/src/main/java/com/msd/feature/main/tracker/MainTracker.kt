package com.msd.feature.main.tracker

import com.msd.core.tracking.Constants.ADD_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.CONFIGURATION_DELETED_EVENT
import com.msd.core.tracking.Constants.DELETE_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.EDIT_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.Constants.OPEN_CONFIGURATION_CLICKED_EVENT
import com.msd.core.tracking.EventsTracker
import javax.inject.Inject

class MainTracker @Inject constructor(private val eventsTracker: EventsTracker) {

    fun logOpenConfigurationClickedEvent() {
        eventsTracker.logEvent(OPEN_CONFIGURATION_CLICKED_EVENT)
    }

    fun logEditConfigurationClickedEvent() {
        eventsTracker.logEvent(EDIT_CONFIGURATION_CLICKED_EVENT)
    }

    fun logAddConfigurationClickedEvent() {
        eventsTracker.logEvent(ADD_CONFIGURATION_CLICKED_EVENT)
    }

    fun logOnDeleteConfigurationClickedEvent() {
        eventsTracker.logEvent(DELETE_CONFIGURATION_CLICKED_EVENT)
    }

    fun logConfigurationDeletedEvent() {
        eventsTracker.logEvent(CONFIGURATION_DELETED_EVENT)
    }
}