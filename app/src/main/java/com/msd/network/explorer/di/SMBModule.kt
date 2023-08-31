package com.msd.network.explorer.di

import com.hierynomus.smbj.SMBClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class SMBModule {

    @Provides
    fun provideSMBClient(): SMBClient = SMBClient()
}