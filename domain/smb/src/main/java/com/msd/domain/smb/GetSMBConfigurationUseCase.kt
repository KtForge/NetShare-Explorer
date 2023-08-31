package com.msd.domain.smb

import com.msd.domain.smb.model.SMBConfiguration
import javax.inject.Inject

class GetSMBConfigurationUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(smbConfigurationId: Int): SMBConfiguration? =
        repository.getSmbConfiguration(smbConfigurationId)
}
