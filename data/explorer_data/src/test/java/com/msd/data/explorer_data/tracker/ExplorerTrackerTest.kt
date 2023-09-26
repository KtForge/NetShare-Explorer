package com.msd.data.explorer_data.tracker

import com.msd.core.tracking.Constants.DOWNLOAD_FILE_EVENT
import com.msd.core.tracking.Constants.FILES_NUMBER
import com.msd.core.tracking.Constants.FILE_SIZE
import com.msd.core.tracking.Constants.LIST_FILES_AND_DIRECTORIES_EVENT
import com.msd.core.tracking.Constants.OPEN_FILE_EVENT
import com.msd.core.tracking.Constants.OPEN_TIME
import com.msd.core.tracking.EventsTracker
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ExplorerTrackerTest {

    private val eventsTracker: EventsTracker = mock()

    private val tracker = ExplorerTracker(eventsTracker)

    @Test
    fun `when tracking list and directories should invoke the tracker`() {
        val expectedParameters = mapOf(
            FILES_NUMBER to 1,
            OPEN_TIME to 1.0,
        )

        tracker.logListFilesAndDirectoriesEvent(filesNumber = 1, openTime = 1000L)

        verify(eventsTracker).logEvent(LIST_FILES_AND_DIRECTORIES_EVENT, expectedParameters)
    }

    @Test
    fun `when tracking open file should invoke the tracker`() {
        val expectedParameters = mapOf(FILE_SIZE to 1.0)

        tracker.logOpenFileEvent(fileSize = 1000000L)

        verify(eventsTracker).logEvent(OPEN_FILE_EVENT, expectedParameters)
    }

    @Test
    fun `when tracking download file should invoke the tracker`() {
        val expectedParameters = mapOf(
            FILE_SIZE to 1.0,
            OPEN_TIME to 1.0,
        )

        tracker.logDownloadFile(fileSize = 1000000L, openTime = 1000L)

        verify(eventsTracker).logEvent(DOWNLOAD_FILE_EVENT, expectedParameters)
    }
}