package com.msd.domain.smb.model

data class SMBConfiguration(
    val id: Int?,
    val name: String,
    val server: String,
    val sharedPath: String,
    val user: String,
    val psw: String,
)
