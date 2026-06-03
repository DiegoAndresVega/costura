package com.example.costura.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class PatronComunidad(
    val id: String = "",
    @get:PropertyName("id_autor") @set:PropertyName("id_autor") var uidAutor: String = "",
    val nombreAutor: String = "",
    val fotoAutorUrl: String? = null,
    val nombre: String = "",
    val categoria: String = "",
    val dificultad: String = "",
    val anchoCm: Float = 0f,
    val largoCm: Float = 0f,
    val descripcion: String = "",
    val consejos: String = "",
    val fotosUrls: List<String> = emptyList(),
    val pdfUrl: String? = null,
    val tutorialUrl: String? = null,
    val likes: Int = 0,
    val fechaPublicacion: Timestamp? = null
)
