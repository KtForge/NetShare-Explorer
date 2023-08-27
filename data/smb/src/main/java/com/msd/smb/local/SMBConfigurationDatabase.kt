package com.msd.smb.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msd.smb.model.DataSMBConfiguration

@Database(entities = [DataSMBConfiguration::class], version = 1)
abstract class SMBConfigurationDatabase : RoomDatabase() {

    abstract fun smbConfigurationDao(): SMBConfigurationDao
}