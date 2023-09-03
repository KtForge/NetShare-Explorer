package com.msd.data.smb_data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msd.data.smb_data.model.DataSMBConfiguration
import kotlinx.coroutines.flow.Flow

@Dao
interface SMBConfigurationDao {

    @Query("SELECT * FROM datasmbconfiguration")
    fun getAll(): Flow<List<DataSMBConfiguration>>

    @Query("SELECT * FROM datasmbconfiguration WHERE id == :id")
    suspend fun get(id: Int): DataSMBConfiguration?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataSMBConfiguration: DataSMBConfiguration)

    @Query("DELETE FROM datasmbconfiguration WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM datasmbconfiguration")
    suspend fun deleteAll()
}
