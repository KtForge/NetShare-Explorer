package com.msd.domain.explorer.model

data class FilesResult(
    val parentDirectory: ParentDirectory?,
    val workingDirectory: WorkingDirectory,
    val filesAndDirectories: List<IBaseFile>,
)