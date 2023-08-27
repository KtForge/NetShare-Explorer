package com.msd.explorer.model

data class NetworkFile(
    override val name: String,
    val path: String,
) : IBaseFile