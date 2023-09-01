package com.msd.network.explorer.steps

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.msd.network.explorer.MainActivity
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import org.junit.Rule

@HiltAndroidTest
class KotlinSteps {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var mActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Given("I am here")
    fun i_am_here() {
        composeRule.onNodeWithText("HEIEIE").assertDoesNotExist()
    }

    @When("^I open compose activity")
    fun i_open_compose_activity() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        //scenarioHolder.launch(instrumentation.context)
    }
}
