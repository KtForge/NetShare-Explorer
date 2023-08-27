package com.msd.smb

import com.msd.smb.local.SMBConfigurationDao
import com.msd.smb.mapper.SMBConfigurationMapper.toData
import com.msd.smb.mapper.SMBConfigurationMapper.toDomain
import com.msd.smb.model.SMBConfiguration
import javax.inject.Inject

class SMBConfigurationRepository @Inject constructor(private val dao: SMBConfigurationDao) :
    ISMBConfigurationRepository {

    override suspend fun getAll(): List<SMBConfiguration> = dao.getAll().toDomain()

    override suspend fun getSmbConfiguration(id: Int): SMBConfiguration = dao.get(id).toDomain()

    override suspend fun insert(smbConfiguration: SMBConfiguration) =
        dao.insert(smbConfiguration.toData())

    override suspend fun delete(id: Int) = dao.delete(id)
}