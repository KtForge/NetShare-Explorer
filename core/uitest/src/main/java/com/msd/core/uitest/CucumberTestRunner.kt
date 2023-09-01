package com.msd.core.uitest

import android.os.Bundle
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["features"],
    glue = ["com.msd.core.uitest.steps"]
)
class CucumberTestRunner : CucumberAndroidJUnitRunner() {

    override fun onCreate(bundle: Bundle?) {

    }
}
