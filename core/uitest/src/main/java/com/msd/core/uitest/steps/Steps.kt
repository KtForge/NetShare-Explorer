package com.msd.core.uitest.steps

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import com.msd.core.uitest.ComposeRuleHolder
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Then

@HiltAndroidTest
class Steps(
    val composeRuleHolder: ComposeRuleHolder,
    val scenarioHolder: ActivityScenarioHolder
): SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Then("I should see {string} on the display")
    fun I_should_see_s_on_the_display(s: String?) {
        composeRuleHolder.composeRule.onNodeWithText("jsjsjs").assertDoesNotExist()
    }
}
