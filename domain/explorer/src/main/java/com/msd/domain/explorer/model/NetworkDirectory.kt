package com.msd.domain.explorer.model

data class NetworkDirectory(
    override val name: String,
    val path: String,
) : IBaseFile
