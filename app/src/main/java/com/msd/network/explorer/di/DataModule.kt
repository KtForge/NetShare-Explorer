package com.msd.network.explorer.di

import com.msd.data.explorer_data.ExplorerRepository
import com.msd.domain.explorer.IExplorerRepository
import com.msd.domain.smb.ISMBConfigurationRepository
import com.msd.data.smb_data.SMBConfigurationRepository
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
