package com.msd.network.explorer.test.steps.logger

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

private const val BEGINNING_TAG = "--------- beginning of main"

object LoggerReader {

    private var filePath: String = ""
    private var filter: String = ""
    private val events: MutableList<String> = mutableListOf()
    private var record: Boolean = false
    private val logToRecord = StringBuilder()
    private val job = CoroutineScope(Job())

    fun initialize(record: Boolean) {
        this.record = record
        filter = ""
        filePath = ""
        logToRecord.clear()
        events.clear()
    }

    fun listenToEvents(filter: String, filePath: String) {
        // Clear logcat
        Runtime.getRuntime().exec("logcat -c")

        // Set variables
        this.filePath = filePath
        this.filter = filter

        // Open file to compare if not recording
        if (!record) {
            val context = InstrumentationRegistry.getInstrumentation().context
            context.assets.open("logs/$filePath").bufferedReader().use { reader ->
                reader.forEachLine { line -> events.add(line) }
            }
        }

        if (filter.isNotEmpty()) {
            job.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val process =
                            Runtime.getRuntime().exec("logcat $filter:I *:S -v raw")
                        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            val shouldWriteLine = !line.isNullOrEmpty() &&
                                    line != BEGINNING_TAG

                            if (shouldWriteLine) {
                                processEvent(line!!)
                            }
                        }
                    } catch (e: IOException) {
                        throw RuntimeException("Can't read the logcat")
                    }
                }
            }
        }
    }

    private fun processEvent(line: String) {
        if (record) {
            saveLine(line)
        } else {
            compareEvent(line)
        }
    }

    private fun saveLine(line: String) {
        logToRecord.appendLine(line)
    }

    private fun compareEvent(line: String) {
        if (line == events.first()) {
            events.removeFirst()
        } else {
            throw RuntimeException("Expected: ${events.first()}, found: $line")
        }
    }

    fun writeLogsIfRecording() {
        job.cancel()
        if (record && filter.isNotEmpty()) {
            // write events on specified file
            if (filePath.isEmpty()) {
                throw RuntimeException("No file to write the logs")
            }
            if (logToRecord.isNotEmpty()) {
                writeFile()
            }
        }
    }

    private fun writeFile() {
        if (filePath.isNotEmpty()) {
            val outputDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

            val relativePath = filePath.substringBeforeLast("/")
            val fileName = filePath.substringAfterLast("/")

            val folder = File(outputDir, "logs/$relativePath")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val file = File(folder, fileName)

            val byteArray = ByteArrayInputStream(logToRecord.toString().toByteArray())
            byteArray.use { _is ->
                FileOutputStream(file).use { output ->
                    _is.copyTo(output)
                }
            }
        }
    }
}
