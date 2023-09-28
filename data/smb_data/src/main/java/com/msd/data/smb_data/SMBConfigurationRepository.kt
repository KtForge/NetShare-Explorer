package com.msd.data.smb_data

import android.content.Context
import com.msd.data.files.FileManager
import com.msd.data.smb_data.local.SMBConfigurationDao
import com.msd.data.smb_data.mapper.SMBConfigurationMapper.toData
import com.msd.data.smb_data.mapper.SMBConfigurationMapper.toDomain
import com.msd.domain.smb.ISMBConfigurationRepository
import com.msd.domain.smb.model.SMBConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SMBConfigurationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: SMBConfigurationDao,
    private val fileManager: FileManager
) : ISMBConfigurationRepository {

    override fun getAll(): Flow<List<SMBConfiguration>> = dao.getAll().map { it.toDomain() }

    override suspend fun getSmbConfiguration(id: Int): SMBConfiguration? = dao.get(id).toDomain()

    override suspend fun insert(smbConfiguration: SMBConfiguration) =
        dao.insert(smbConfiguration.toData())

    override suspend fun delete(id: Int) {
        dao.get(id)?.let { configuration ->
            fileManager.deleteServerContents(configuration.server, configuration.sharedPath)
        }

        dao.delete(id)
    }
}
