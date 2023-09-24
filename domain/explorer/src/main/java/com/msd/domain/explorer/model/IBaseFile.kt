package com.msd.domain.explorer.model

sealed interface IBaseFile {
    val name: String
    val path: String
}
