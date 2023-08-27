package com.msd.smb

import com.msd.smb.model.SMBConfiguration
import javax.inject.Inject

class GetSMBConfigurationUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(smbConfigurationId: Int): SMBConfiguration =
        repository.getSmbConfiguration(smbConfigurationId)
}