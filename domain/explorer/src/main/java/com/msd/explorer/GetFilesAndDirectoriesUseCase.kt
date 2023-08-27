package com.msd.explorer

import com.msd.explorer.model.IBaseFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilesAndDirectoriesUseCase @Inject constructor(private val repository: IExplorerRepository) {

    suspend operator fun invoke(
        server: String,
        sharedPath: String,
        directoryRelativePath: String,
        user: String,
        psw: String
    ): List<IBaseFile> = withContext(Dispatchers.IO) {
        repository.retrieveFilesAndDirectories(server, sharedPath, directoryRelativePath, user, psw)
    }
}