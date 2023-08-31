package com.msd.data.smb_data.mapper

import com.msd.data.smb_data.model.DataSMBConfiguration
import com.msd.domain.smb.model.SMBConfiguration

object SMBConfigurationMapper {

    fun List<DataSMBConfiguration>.toDomain(): List<SMBConfiguration> {
        return mapNotNull { dataSMBConfiguration -> dataSMBConfiguration.toDomain() }
    }

    fun DataSMBConfiguration?.toDomain(): SMBConfiguration? = this?.let {
            SMBConfiguration(
                id = id,
                name = name.takeUnless { it.isNullOrEmpty() } ?: server,
                server = server,
                sharedPath = sharedPath,
                user = user,
                psw = psw
            )
        }

    fun SMBConfiguration.toData(): DataSMBConfiguration = DataSMBConfiguration(
        id = id ?: 0,
        name = name,
        server = server,
        sharedPath = sharedPath,
        user = user,
        psw = psw
    )
}
