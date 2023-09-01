package com.msd.network.explorer

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import org.junit.After

class ActivityScenarioHolder {

    private var scenario: ActivityScenario<MainActivity>? = null

    fun launch(context: Context) {
        scenario = ActivityScenario.launch(Intent(context, MainActivity::class.java))
    }

    /**
     *  Close activity after scenario
     */
    @After
    fun close() {
        scenario?.close()
    }
}