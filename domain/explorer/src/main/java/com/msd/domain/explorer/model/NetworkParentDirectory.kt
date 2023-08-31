package com.msd.domain.explorer.model

data class NetworkParentDirectory(
    override val name: String,
    val path: String
) : IBaseFile
