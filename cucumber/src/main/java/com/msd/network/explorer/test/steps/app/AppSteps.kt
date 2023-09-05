package com.msd.network.explorer.test.steps.app

import com.msd.network.explorer.test.steps.ComposeRuleHolder
import io.cucumber.java.en.Given

class AppSteps {

    private val composeRuleHolder: ComposeRuleHolder = ComposeRuleHolder()

    @Given("^I initialize the App")
    fun initializeApp() {
        composeRuleHolder.launchApp()
    }

    @Given("I insert initial data")
    fun i_insert_initial_data() {
        composeRuleHolder.insertInitialData()
    }
}
