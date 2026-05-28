package com.example.costura.model

data class Usuario(
    val uid: String = "",
    val nombreUsuario: String = "",
    val email: String = "",
    val fotoUrl: String? = null,
    val nombreMostrado: String = "",
    val bio: String = "",
    val nivelCostura: String = "",
    val fotoPerfilUrl: String? = null
)
