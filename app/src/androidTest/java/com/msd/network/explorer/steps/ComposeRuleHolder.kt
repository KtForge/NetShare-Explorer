package com.msd.network.explorer.steps

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.msd.data.smb_data.local.SMBConfigurationDatabase
import com.msd.data.smb_data.model.DataSMBConfiguration
import com.msd.network.explorer.MainActivity
import io.cucumber.java.After
import io.cucumber.junit.WithJunitRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.junit.Rule

@WithJunitRule
class ComposeRuleHolder {

    private var scenarioRule: ActivityScenario<MainActivity>? = null

    @get:Rule
    val composeRule = createEmptyComposeRule()

    fun launchApp() {
        scenarioRule = ActivityScenario.launch(MainActivity::class.java)
    }

    fun insertInitialData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        CoroutineScope(Job()).launch {
            SMBConfigurationDatabase.getInstance(context.applicationContext).smbConfigurationDao()
                .insert(
                    DataSMBConfiguration(
                        id = 0,
                        name = "Linux",
                        server = "192.168.1.185",
                        sharedPath = "Public",
                        user = "User",
                        psw = "Password",
                    )
                )
        }
    }

    @After
    fun close() {
        scenarioRule?.close()
    }
}