package com.msd.smb

import com.msd.smb.model.SMBConfiguration

interface ISMBConfigurationRepository {

    suspend fun getAll(): List<SMBConfiguration>

    suspend fun getSmbConfiguration(id: Int) : SMBConfiguration

    suspend fun insert(smbConfiguration: SMBConfiguration)

    suspend fun delete(id: Int)
}