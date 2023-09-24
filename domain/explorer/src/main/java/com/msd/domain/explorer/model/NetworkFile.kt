package com.msd.domain.explorer.model

data class NetworkFile(
    override val name: String,
    override val path: String,
    val localPath: String,
    val isLocal: Boolean,
) : IBaseFile
