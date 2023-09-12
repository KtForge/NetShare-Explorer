package com.msd.domain.explorer

import javax.inject.Inject

class OpenFileUseCase @Inject constructor(private val repository: IExplorerRepository) {

    @Throws(Exception::class)
    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        absolutePath: String,
        fileName: String,
        user: String,
        psw: String,
        progressListener: (Float) -> Unit,
    ) = repository.openFile(server, sharedPath, absolutePath, fileName, user, psw, progressListener)
}
