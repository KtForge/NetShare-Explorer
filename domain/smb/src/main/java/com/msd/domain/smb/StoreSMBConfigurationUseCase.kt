package com.msd.domain.smb

import com.msd.domain.smb.model.SMBConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StoreSMBConfigurationUseCase @Inject constructor(private val repository: ISMBConfigurationRepository) {

    suspend operator fun invoke(
        id: Int?,
        name: String?,
        server: String,
        sharedPath: String,
        user: String,
        psw: String,
    ) = withContext(Dispatchers.IO) {
        repository.insert(
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
}
