package com.msd.network.explorer.steps.app

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.msd.network.explorer.steps.ComposeRuleHolder
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Given
import io.cucumber.junit.WithJunitRule

@WithJunitRule
@HiltAndroidTest
class AppSteps(
    val composeRuleHolder: ComposeRuleHolder
) : SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Given("^I initialize the App")
    fun initializeApp() {
        composeRuleHolder.launchApp()
    }

    @Given("I insert initial data")
    fun i_insert_initial_data() {
        composeRuleHolder.insertInitialData()
    }
}
