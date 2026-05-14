package com.example.costura.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patrones_precargados")
data class PatronLocal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val anchoCm: Float,
    val largoCm: Float,
    val dificultad: String,
    val descripcion: String,
    val imagenAsset: String = "",
    val fuente: String = ""
)
