package com.msd.network.explorer.test.steps.logger

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
    private var fileName: String = ""

    fun initialize() {
        Runtime.getRuntime().exec("logcat -c")
        log.clear()
        alreadyWrittenLog.clear()
        counter = 0
        filter = ""
        fileName = ""
    }

    fun listenToEvents(filter: String, fileName: String) {
        this.filter = filter
        this.fileName = fileName
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
                    val shouldWriteLine = !line.isNullOrEmpty() &&
                            line?.contains("$filter:", ignoreCase = true) == true &&
                            !alreadyWrittenLog.contains(line.orEmpty())
                    if (shouldWriteLine) {
                        log.appendLine("STEP: $counter -> $line")
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
        if (fileName.isNotEmpty()) {
            val outputDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
            val folder = File(outputDir, "logs")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val file = File(folder, fileName)

            val byteArray = ByteArrayInputStream(log.toString().toByteArray())
            byteArray.use { _is ->
                FileOutputStream(file).use { output ->
                    _is.copyTo(output)
                }
            }
        }
    }
}