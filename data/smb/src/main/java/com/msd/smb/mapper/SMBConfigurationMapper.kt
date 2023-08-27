package com.msd.smb.mapper

import com.msd.smb.model.DataSMBConfiguration
import com.msd.smb.model.SMBConfiguration

object SMBConfigurationMapper {

    fun List<DataSMBConfiguration>.toDomain(): List<SMBConfiguration> {
        return map { dataSMBConfiguration -> dataSMBConfiguration.toDomain() }
    }

    fun DataSMBConfiguration.toDomain(): SMBConfiguration = SMBConfiguration(
        id = id,
        name = name.takeUnless { it.isNullOrEmpty() } ?: server,
        server = server,
        sharedPath = sharedPath,
        user = user,
        psw = psw
    )

    fun SMBConfiguration.toData(): DataSMBConfiguration = DataSMBConfiguration(
        id = id ?: 0,
        name = name,
        server = server,
        sharedPath = sharedPath,
        user = user,
        psw = psw
    )
}