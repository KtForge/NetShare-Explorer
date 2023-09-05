package com.msd.network.explorer.test.steps.edit

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.msd.network.explorer.test.steps.ComposeRuleHolder
import dagger.hilt.android.testing.HiltAndroidTest
import io.cucumber.java.en.Then
import io.cucumber.junit.WithJunitRule

@WithJunitRule
@HiltAndroidTest
class EditSteps(
    val composeRuleHolder: ComposeRuleHolder
) : SemanticsNodeInteractionsProvider by composeRuleHolder.composeRule {

    @Then("I type {string} in field {string}")
    fun i_type_text_in_position_field(text: String, label: String) {
        val contentDescription = when (label) {
            "Name" -> "Enter an optional name"
            "Server" -> "Enter the server address"
            "Shared path" -> "Enter the shared path"
            "User" -> "Enter the optional user credential"
            "Password" -> "Enter the optional password credential"
            else -> throw IllegalArgumentException()
        }

        onNodeWithContentDescription(contentDescription, useUnmergedTree = true)
            .performTextInput(
                text
            )
        onNodeWithText(text).assertIsDisplayed()
    }

    @Then("I reveal the password field")
    fun i_reveal_the_password_field() {
        val contentDescription = "Show password"
        onNodeWithContentDescription(contentDescription, useUnmergedTree = true).performClick()
    }

    @Then("I see error for field {string}")
    fun i_see_error_for_field(label: String) {
        val text = when (label) {
            "Server" -> "Server can't be empty"
            "Shared path" -> "Shared path can't be empty"
            else -> throw IllegalArgumentException()
        }

        onNodeWithText(text).assertIsDisplayed()
    }

    @Then("I click the save button")
    fun i_click_the_confirm_button() {
        val text = "Save"
        onNodeWithText(text).performClick()
    }
}