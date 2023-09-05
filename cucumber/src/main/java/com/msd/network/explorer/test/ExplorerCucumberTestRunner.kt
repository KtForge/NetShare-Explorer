package com.msd.network.explorer.test

import android.app.Application
import android.content.Context
import android.os.Bundle
import dagger.hilt.android.testing.HiltTestApplication
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import java.io.File

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["features"],
    glue = ["com.msd.network.explorer.test.steps"],
)
class ExplorerCucumberTestRunner : CucumberAndroidJUnitRunner() {

    override fun onCreate(bundle: Bundle) {
        bundle.putString(
            "plugin",
            getPluginConfigurationString()
        )
        File(getAbsoluteFilesPath()).mkdirs()
        super.onCreate(bundle)
    }

    private fun getPluginConfigurationString(): String {
        return "json:" + getCucumberJson()
    }

    private fun getCucumberJson(): String {
        return getAbsoluteFilesPath() + "/cucumber.json"
    }

    private fun getAbsoluteFilesPath(): String {
        val directory = targetContext.cacheDir
        val file = File(directory, "/reports/cucumber")

        return file.absolutePath
    }

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
