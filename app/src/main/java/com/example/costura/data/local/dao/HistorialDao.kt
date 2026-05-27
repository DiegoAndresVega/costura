package com.example.costura.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.costura.data.local.entity.HistorialMedicion
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {

    @Query("SELECT * FROM historial_mediciones ORDER BY fecha DESC")
    fun getAll(): Flow<List<HistorialMedicion>>

    @Insert
    suspend fun insert(medicion: HistorialMedicion)
}
