package com.msd.core.tracking

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class EventsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(eventName: String, parameters: Bundle = bundleOf()) {
        firebaseAnalytics.logEvent(eventName, parameters)
    }
}