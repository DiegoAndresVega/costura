package com.example.costura.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_mediciones")
data class HistorialMedicion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val anchoCm: Float,
    val largoCm: Float,
    val fecha: String,
    val patronesEncajan: String
)
