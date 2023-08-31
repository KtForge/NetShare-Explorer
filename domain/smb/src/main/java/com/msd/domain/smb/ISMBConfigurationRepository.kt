package com.msd.domain.smb

import com.msd.domain.smb.model.SMBConfiguration
import kotlinx.coroutines.flow.Flow

interface ISMBConfigurationRepository {

    fun getAll(): Flow<List<SMBConfiguration>>

    suspend fun getSmbConfiguration(id: Int) : SMBConfiguration?

    suspend fun insert(smbConfiguration: SMBConfiguration)

    suspend fun delete(id: Int)
}
