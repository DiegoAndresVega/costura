package com.example.costura.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.costura.data.local.entity.PatronLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface PatronDao {

    @Query("SELECT * FROM patrones_precargados ORDER BY nombre ASC")
    fun getAll(): Flow<List<PatronLocal>>

    @Query("""
        SELECT * FROM patrones_precargados
        WHERE anchoCm <= :ancho AND largoCm <= :largo
        ORDER BY nombre ASC
    """)
    fun getQueEncajan(ancho: Float, largo: Float): Flow<List<PatronLocal>>

    @Query("""
        SELECT * FROM patrones_precargados
        WHERE anchoCm > :ancho OR largoCm > :largo
        ORDER BY nombre ASC
    """)
    fun getQueNoEncajan(ancho: Float, largo: Float): Flow<List<PatronLocal>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(patrones: List<PatronLocal>)

    @Query("SELECT * FROM patrones_precargados WHERE id = :id")
    suspend fun getById(id: Int): PatronLocal?

    @Query("SELECT COUNT(*) FROM patrones_precargados")
    suspend fun count(): Int

    @Query("SELECT * FROM patrones_precargados ORDER BY nombre ASC")
    suspend fun getAllList(): List<PatronLocal>

    @Query("DELETE FROM patrones_precargados")
    suspend fun deleteAll()
}
