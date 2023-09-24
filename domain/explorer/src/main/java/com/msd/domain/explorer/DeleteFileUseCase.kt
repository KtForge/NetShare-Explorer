package com.msd.domain.explorer

import com.msd.domain.explorer.model.NetworkFile
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(private val repository: IExplorerRepository) {

    operator fun invoke(file: NetworkFile) = repository.deleteFile(file.path)
}