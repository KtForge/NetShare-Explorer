package com.msd.network.explorer.test.steps.logger

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

object LoggerReader {

    private var counter = 0
    private val log = StringBuilder()
    private val alreadyWrittenLog = StringBuilder()
    private var filter: String = ""
    private var comparisonFile: String = ""

    fun initialize() {
        Runtime.getRuntime().exec("logcat -c")
        log.clear()
        alreadyWrittenLog.clear()
        counter = 0
        val outputDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        val folder = File(outputDir.absolutePath + "/logs")
        folder.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            }
        }
    }

    fun listenToEvents(filter: String, comparisonFile: String) {
        this.filter = filter
    }

    fun readLogCat() {
        if (filter.isNotEmpty()) {
            try {
                val process = Runtime.getRuntime().exec("logcat -d") // ${filter.uppercase()}
                val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    Log.d("SUSU", line.orEmpty())
                    val shouldWriteLine = !line.isNullOrEmpty() &&
                            line?.contains("$filter:", ignoreCase = true) == true &&
                            !alreadyWrittenLog.contains(line.orEmpty())
                    if (shouldWriteLine) {
                        log.appendLine(line)
                    }
                }
                printLog()
            } catch (e: IOException) {
                throw RuntimeException("Can't read the logcat")
            }
        }
    }

    private fun printLog() {
        if (log.isNotEmpty()) {
            writeFile()
            counter++
            log.lines().forEach { line ->
                alreadyWrittenLog.appendLine(line)
            }
            log.clear()
        }
    }

    private fun writeFile() {
        val outputDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        val folder = File(outputDir, "logs")
        if (!folder.exists()) {
            folder.mkdir()
        }

        val file = File(outputDir, "logs/logcat_$counter.txt")

        val byteArray = ByteArrayInputStream(log.toString().toByteArray())
        byteArray.use { _is ->
            FileOutputStream(file).use { output ->
                _is.copyTo(output)
            }
        }
    }
}