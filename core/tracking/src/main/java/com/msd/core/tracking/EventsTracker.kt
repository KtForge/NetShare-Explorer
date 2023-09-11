package com.msd.core.tracking

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import org.json.JSONObject
import javax.inject.Inject

private const val TAG = "TRACKING"

class EventsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(eventName: String, parameters: Bundle = bundleOf()) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, buildEventJson(eventName, parameters).toString())
        } else {
            firebaseAnalytics.logEvent(eventName, parameters)
        }
    }

    private fun buildEventJson(eventName: String, parameters: Bundle): JSONObject {
        val json = JSONObject()
        json.put("event_name", eventName)

        val parametersJson = JSONObject()
        parameters.keySet().forEach { key ->
            parametersJson.put(key, parameters.getString(key))
        }

        json.put("parameters", parametersJson)

        return json
    }
}