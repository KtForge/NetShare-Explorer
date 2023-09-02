package com.msd.network.explorer.steps.main

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msd.network.explorer.steps.ComposeRuleHolder
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Then
import io.cucumber.junit.WithJunitRule

@WithJunitRule
@HiltAndroidTest
class MainSteps(
    val composeRuleHolder: ComposeRuleHolder
) : SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Then("I am on Main screen")
    fun i_am_on_main_screen() {
        val text = "NetShare Explorer"
        onNodeWithText(text).assertIsDisplayed()
    }

    @Then("I see {string} element")
    fun i_see_element(text: String) {
        onNodeWithText(text).assertIsDisplayed()
    }

    @Then("I click on add network configuration button")
    fun i_click_on_add_network() {
        val text = "Configure a network location"
        onNodeWithText(text).performClick()
    }
}