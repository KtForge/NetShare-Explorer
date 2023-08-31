package com.msd.data.smb_data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataSMBConfiguration(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String? = null,
    val server: String = "",
    @ColumnInfo(name = "shared_path") val sharedPath: String = "",
    val user: String = "",
    val psw: String = "",
)
