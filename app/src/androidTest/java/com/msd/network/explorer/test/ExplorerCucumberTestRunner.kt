package com.msd.network.explorer.test

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import dagger.hilt.android.testing.HiltTestApplication
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import java.io.File

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["features"],
    glue = ["com.msd.network.explorer.steps"],
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
        val directory = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return File(directory, "reports/cucumber").absolutePath
    }

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
