package com.msd.domain.explorer

import com.msd.domain.explorer.model.IBaseFile
import javax.inject.Inject

class GetFilesAndDirectoriesUseCase @Inject constructor(private val repository: IExplorerRepository) {

    @Throws(Exception::class)
    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        absolutePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> =
        repository.retrieveFilesAndDirectories(server, sharedPath, absolutePath, user, psw)
}
