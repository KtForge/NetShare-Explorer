package com.msd.domain.explorer

import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(private val repository: IExplorerRepository) {

    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        absolutePath: String,
        fileName: String,
        user: String,
        psw: String,
    ) = repository.downloadFile(server, sharedPath, absolutePath, fileName, user, psw)
}
