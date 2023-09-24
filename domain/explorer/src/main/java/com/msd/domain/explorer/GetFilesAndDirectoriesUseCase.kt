package com.msd.domain.explorer

import com.msd.domain.explorer.model.FilesResult
import javax.inject.Inject

class GetFilesAndDirectoriesUseCase @Inject constructor(private val repository: IExplorerRepository) {

    @Throws(Exception::class)
    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        directoryPath: String,
        user: String,
        psw: String
    ): FilesResult =
        repository.retrieveFilesAndDirectories(server, sharedPath, directoryPath, user, psw)
}
