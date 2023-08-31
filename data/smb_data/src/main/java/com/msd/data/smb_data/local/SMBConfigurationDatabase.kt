package com.msd.data.smb_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msd.data.smb_data.model.DataSMBConfiguration

@Database(entities = [DataSMBConfiguration::class], version = 1)
abstract class SMBConfigurationDatabase : RoomDatabase() {

    abstract fun smbConfigurationDao(): SMBConfigurationDao
}
