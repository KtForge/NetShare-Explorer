package com.msd.domain.explorer.model

data class NetworkFile(
    override val name: String,
    val path: String,
) : IBaseFile
