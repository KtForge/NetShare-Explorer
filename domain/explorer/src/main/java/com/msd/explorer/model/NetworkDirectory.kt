package com.msd.explorer.model

data class NetworkDirectory(
    override val name: String,
    val path: String,
) : IBaseFile