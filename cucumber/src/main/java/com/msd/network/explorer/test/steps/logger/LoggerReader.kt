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
    private var filter: String = ""
    private var fileName: String = ""
    private val events: MutableList<String> = mutableListOf()
    private var record: Boolean = false
    private var logToRecord: StringBuilder = StringBuilder()

    fun initialize(record: Boolean) {
        Runtime.getRuntime().exec("logcat -c")
        this.record = record
        counter = 0
        filter = ""
        fileName = ""
        logToRecord.clear()
        events.clear()
    }

    fun listenToEvents(filter: String, fileName: String) {
        this.filter = filter
        this.fileName = fileName
        if (!record) {
            val context = InstrumentationRegistry.getInstrumentation().context
            context.assets.open("logs/$fileName").bufferedReader().use { reader ->
                reader.forEachLine { line -> events.add(line) }
            }
        }
    }

    fun readLogCat() {
        if (filter.isNotEmpty()) {
            try {
                val process = Runtime.getRuntime().exec("logcat -d")
                val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )
                val log = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    val shouldWriteLine = !line.isNullOrEmpty() &&
                            line?.contains("$filter:", ignoreCase = true) == true
                    if (shouldWriteLine) {
                        val cleanLine = line?.replaceBefore("${filter.uppercase()}:", "")
                        log.appendLine("STEP: $counter -> $cleanLine")
                    }
                }
                processLog(log)
                // Clean logcat for next step
                Runtime.getRuntime().exec("logcat -c")
            } catch (e: IOException) {
                throw RuntimeException("Can't read the logcat")
            }
            counter++
        }
    }

    private fun processLog(log: StringBuilder) {
        if (record) {
            saveLogs(log)
        } else {
            compareEvents(log)
        }
    }

    private fun saveLogs(log: StringBuilder) {
        val byteArray = ByteArrayInputStream(log.toString().toByteArray())
        byteArray.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                logToRecord.appendLine(line)
            }
        }
    }

    // compare first line from comparison file with event and discard first line of file if match
    private fun compareEvents(log: StringBuilder) {
        val byteArray = ByteArrayInputStream(log.toString().toByteArray())
        byteArray.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                if (line == events.first()) {
                    events.removeFirst()
                } else {
                    throw RuntimeException("Expected: ${events.first()}, found: $line")
                }
            }
        }
    }

    fun writeLogsIfRecording() {
        if (record) {
            // write events on specified file
            if (fileName.isEmpty()) {
                throw RuntimeException("No file to write the logs")
            }
            if (logToRecord.isNotEmpty()) {
                writeFile(logToRecord)
            }
        }
    }

    private fun writeFile(log: StringBuilder) {
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
