package com.msd.explorer.model

data class NetworkParentDirectory(
    override val name: String,
    val path: String
) : IBaseFile