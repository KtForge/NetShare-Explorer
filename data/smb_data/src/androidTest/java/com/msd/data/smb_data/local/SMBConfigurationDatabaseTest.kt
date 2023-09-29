package com.msd.data.smb_data.local

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

class SMBConfigurationDatabaseTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun gettingDatabaseInstanceShouldReturnAlwaysTheSameInstance() {
        val database = SMBConfigurationDatabase.getInstance(context.applicationContext)

        val secondReference = SMBConfigurationDatabase.getInstance(context.applicationContext)

        assert(database == secondReference)
    }
}