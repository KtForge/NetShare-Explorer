package com.msd.data.explorer_data.mapper

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.msd.data.files.FileManager
import com.msd.domain.explorer.model.FilesResult

interface IFilesAndDirectoriesMapper {

    fun buildFilesResult(
        server: String,
        sharedPath: String,
        parentPath: String,
        files: List<FileIdBothDirectoryInformation>,
        fileManager: FileManager,
    ): FilesResult
}
