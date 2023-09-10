package com.msd.data.explorer_data.tracker

import androidx.core.os.bundleOf
import com.msd.core.tracking.Constants
import com.msd.core.tracking.EventsTracker
import javax.inject.Inject

class ExplorerTracker @Inject constructor(private val eventsTracker: EventsTracker) {

    fun logListFilesAndDirectoriesEvent(filesNumber: Int, openTime: Long) {
        val readableOpenTime = openTime.toDouble().div(1000)

        val parameters = bundleOf(
            Constants.FILES_NUMBER to filesNumber,
            Constants.OPEN_TIME to readableOpenTime
        )

        eventsTracker.logEvent(Constants.LIST_FILES_AND_DIRECTORIES_EVENT, parameters)
    }

    fun logOpenFileEvent(fileSize: Long, openTime: Long) {
        val readableFileSize = fileSize.toDouble().div(1000000)
        val readableOpenTime = openTime.toDouble().div(1000)

        val parameters = bundleOf(
            Constants.FILE_SIZE to readableFileSize,
            Constants.OPEN_TIME to readableOpenTime
        )

        eventsTracker.logEvent(Constants.OPEN_FILE_EVENT, parameters)
    }
}