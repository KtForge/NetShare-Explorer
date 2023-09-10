package com.msd.core.tracking

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class EventsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(eventName: String, parameters: Bundle) {
        firebaseAnalytics.logEvent(eventName, parameters)
    }
}