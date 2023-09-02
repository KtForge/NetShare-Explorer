package com.msd.network.explorer.di

import android.content.Context
import com.msd.data.smb_data.local.SMBConfigurationDao
import com.msd.data.smb_data.local.SMBConfigurationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideSMBConfigurationDao(smbConfigurationDatabase: SMBConfigurationDatabase): SMBConfigurationDao {
        return smbConfigurationDatabase.smbConfigurationDao()
    }

    @Provides
    @Singleton
    fun provideSMBConfigurationDatabase(@ApplicationContext appContext: Context): SMBConfigurationDatabase {
        return SMBConfigurationDatabase.getInstance(appContext)
    }
}
