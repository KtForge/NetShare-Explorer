package com.msd.smb.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msd.smb.model.DataSMBConfiguration

@Dao
interface SMBConfigurationDao {

    @Query("SELECT * FROM datasmbconfiguration")
    suspend fun getAll(): List<DataSMBConfiguration>

    @Query("SELECT * FROM datasmbconfiguration WHERE id == :id")
    suspend fun get(id: Int): DataSMBConfiguration

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataSMBConfiguration: DataSMBConfiguration)

    @Query("DELETE FROM datasmbconfiguration WHERE id = :id")
    suspend fun delete(id: Int)
}