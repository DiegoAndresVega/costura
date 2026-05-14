package com.example.costura.model

import com.google.firebase.Timestamp

data class Comentario(
    val id: String = "",
    val uidAutor: String = "",
    val nombreUsuario: String = "",
    val texto: String = "",
    val fecha: Timestamp? = null
)
