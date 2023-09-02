package com.msd.network.explorer.steps

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.msd.network.explorer.MainActivity
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.junit.WithJunitRule
import org.junit.Rule

@WithJunitRule
class ComposeRuleHolder {

    private var scenarioRule: ActivityScenario<MainActivity>? = null

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Before
    fun setup() {
        scenarioRule = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun close() {
        scenarioRule?.close()
    }
}