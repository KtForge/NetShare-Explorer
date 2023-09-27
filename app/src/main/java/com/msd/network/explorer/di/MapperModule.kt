package com.msd.network.explorer.di

import com.msd.data.explorer_data.mapper.FilesAndDirectoriesMapper
import com.msd.data.explorer_data.mapper.IFilesAndDirectoriesMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class MapperModule {

    @Provides
    fun provideFilesAndDirectoriesMapper(): IFilesAndDirectoriesMapper = FilesAndDirectoriesMapper
}
