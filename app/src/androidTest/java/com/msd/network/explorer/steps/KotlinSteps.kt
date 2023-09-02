package com.msd.network.explorer.steps

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.WithJunitRule

@WithJunitRule
@HiltAndroidTest
class KotlinSteps(
    val composeRuleHolder: ComposeRuleHolder
) : SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Given("^I initialize App")
    fun initializeApp() {
    }

    @Given("I am here")
    fun i_am_here() {
        onNodeWithText("You have no network configurations, add one!").assertIsDisplayed()
    }

    @Then("I click on add network")
    fun i_click_on_add_network() {
        onNodeWithText("Configure a network location").performClick()
    }

    @Then("I enter a new server")
    fun i_enter_a_new_server() {
        onAllNodesWithText("")[0].performTextInput("Name")
        composeRuleHolder.composeRule.waitForIdle()
        onNodeWithText("Name").assertIsDisplayed()
    }

    @When("^I open compose activity")
    fun i_open_compose_activity() {
    }
}
