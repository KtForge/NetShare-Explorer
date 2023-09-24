package com.msd.domain.explorer

import javax.inject.Inject
import kotlin.jvm.Throws

class DownloadFileUseCase @Inject constructor(private val repository: IExplorerRepository) {

    @Throws(Exception::class)
    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        fileName: String,
        filePath: String,
        localFilePath: String,
        user: String,
        psw: String,
    ) = repository.downloadFile(server, sharedPath, fileName, filePath, localFilePath, user, psw)
}
