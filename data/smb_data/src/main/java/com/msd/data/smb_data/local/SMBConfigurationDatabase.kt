package com.msd.data.smb_data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msd.data.smb_data.model.DataSMBConfiguration

@Database(entities = [DataSMBConfiguration::class], version = 1)
abstract class SMBConfigurationDatabase : RoomDatabase() {

    abstract fun smbConfigurationDao(): SMBConfigurationDao

    companion object {

        private var INSTANCE: SMBConfigurationDatabase? = null

        fun getInstance(context: Context): SMBConfigurationDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    SMBConfigurationDatabase::class.java,
                    "SMBConfiguration"
                ).build()
            }
            return INSTANCE!!
        }
    }
}
