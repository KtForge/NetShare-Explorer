package com.msd.smb

import com.msd.smb.model.SMBConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSMBConfigurationsUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(): List<SMBConfiguration> =
        withContext(Dispatchers.IO) { repository.getAll() }
}