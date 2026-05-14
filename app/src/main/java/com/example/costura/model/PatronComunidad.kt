package com.example.costura.model

import com.google.firebase.Timestamp

data class PatronComunidad(
    val id: String = "",
    val uidAutor: String = "",
    val nombreAutor: String = "",
    val fotoAutorUrl: String? = null,
    val nombre: String = "",
    val categoria: String = "",
    val anchoCm: Float = 0f,
    val largoCm: Float = 0f,
    val descripcion: String = "",
    val fotoUrl: String? = null,
    val pdfUrl: String? = null,
    val tutorialUrl: String? = null,
    val likes: Int = 0,
    val fechaPublicacion: Timestamp? = null
)
