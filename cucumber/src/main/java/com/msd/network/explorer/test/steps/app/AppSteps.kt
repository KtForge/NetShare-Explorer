package com.msd.network.explorer.test.steps.app

import com.msd.network.explorer.test.steps.ComposeRuleHolder
import com.msd.network.explorer.test.steps.logger.LoggerReader
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

    @Given("I start listening for {string} with {string} file")
    fun i_start_listening_for(filter: String, filePath: String) {
        LoggerReader.listenToEvents(filter, filePath)
    }
}
