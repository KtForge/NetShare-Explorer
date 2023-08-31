package com.msd.domain.smb

import com.msd.domain.smb.model.SMBConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSMBConfigurationsUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    operator fun invoke(): Flow<List<SMBConfiguration>> = repository.getAll()
}
