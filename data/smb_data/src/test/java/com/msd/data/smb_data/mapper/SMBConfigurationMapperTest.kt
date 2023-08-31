package com.msd.data.smb_data.mapper

import com.msd.data.smb_data.mapper.SMBConfigurationMapper.toData
import com.msd.data.smb_data.mapper.SMBConfigurationMapper.toDomain
import com.msd.data.smb_data.model.DataSMBConfiguration
import com.msd.domain.smb.model.SMBConfiguration
import org.junit.Test

class SMBConfigurationMapperTest {

    private val smbConfiguration = SMBConfiguration(
        id = 0,
        name = "Name",
        server = "Server",
        sharedPath = "SharedPath",
        user = "User",
        psw = "Psw",
    )

    private val dataSMBConfiguration = DataSMBConfiguration(
        id = 0,
        name = "Name",
        server = "Server",
        sharedPath = "SharedPath",
        user = "User",
        psw = "Psw",
    )

    @Test
    fun `when mapping a list of data objects to domain should return the expected models`() {
        val result = listOf(dataSMBConfiguration).toDomain()

        assert(result == listOf(smbConfiguration))
    }

    @Test
    fun `when mapping a data object to domain should return the expected model`() {
        val result = dataSMBConfiguration.toDomain()

        assert(result == smbConfiguration)
    }

    @Test
    fun `when mapping a domain object to data should return the expected model`() {
        val result = smbConfiguration.toData()

        assert(result == dataSMBConfiguration)
    }
}