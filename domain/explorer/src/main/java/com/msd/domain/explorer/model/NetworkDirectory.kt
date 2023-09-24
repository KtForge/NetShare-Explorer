package com.msd.domain.explorer.model

data class NetworkDirectory(
    override val name: String,
    override val path: String,
    val absolutePath: String,
) : IBaseFile
