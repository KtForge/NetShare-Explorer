package com.msd.network.explorer.test

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
        ) // we programmatically create the plugin configuration
        //it crashes on Android R without it
        File(getAbsoluteFilesPath()).mkdirs()
        super.onCreate(bundle)
    }

    /**
     * Since we want to checkout the external storage directory programmatically, we create the plugin configuration
     * here, instead of the [CucumberOptions] annotation.
     *
     * @return the plugin string for the configuration, which contains XML, HTML and JSON paths
     */
    private fun getPluginConfigurationString(): String? {
        val cucumber = "cucumber"
        val separator = "--"
        return "pretty"
        // return "junit:" + getCucumberXml(cucumber) + separator +
        //         "html:" + getCucumberHtml(cucumber)
    }

    private fun getCucumberHtml(cucumber: String): String {
        return getAbsoluteFilesPath() + "/" + cucumber + ".html"
    }

    private fun getCucumberXml(cucumber: String): String {
        return getAbsoluteFilesPath() + "/" + cucumber + ".xml"
    }

    /**
     * The path which is used for the report files.
     *
     * @return the absolute path for the report files
     */
    private fun getAbsoluteFilesPath(): String {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return File(directory, "reports").absolutePath
    }

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
