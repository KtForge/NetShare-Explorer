package com.msd.domain.smb

import com.msd.domain.smb.model.SMBConfiguration
import javax.inject.Inject

class StoreSMBConfigurationUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(
        id: Int?,
        name: String?,
        server: String,
        sharedPath: String,
        user: String,
        psw: String,
    ) = repository.insert(
        SMBConfiguration(
            id = id,
            name = name ?: "",
            server = server,
            sharedPath = sharedPath,
            user = user,
            psw = psw
        )
    )
}
