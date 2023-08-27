package com.msd.networkstorageconfiguration.di

import com.msd.explorer.ExplorerRepository
import com.msd.explorer.IExplorerRepository
import com.msd.smb.ISMBConfigurationRepository
import com.msd.smb.SMBConfigurationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {

    @Binds
    abstract fun bindSMBConfigurationRepository(smbConfigurationRepository: SMBConfigurationRepository): ISMBConfigurationRepository

    @Binds
    abstract fun bindExplorerRepository(explorerRepository: ExplorerRepository): IExplorerRepository
}