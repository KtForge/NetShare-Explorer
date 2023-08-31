package com.msd.domain.smb

import javax.inject.Inject

class DeleteSMBConfigurationUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(id: Int) = repository.delete(id)
}
