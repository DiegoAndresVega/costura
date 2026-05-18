package com.example.costura.model

data class Usuario(
    val uid: String = "",
    val nombreUsuario: String = "",   // de Google Auth, no editable
    val email: String = "",
    val fotoUrl: String? = null,      // foto de Google, fallback
    // campos editables guardados en Firestore:
    val nombreMostrado: String = "",  // nombre que ven los demás
    val bio: String = "",
    val nivelCostura: String = "",    // "principiante" | "intermedio" | "avanzado"
    val fotoPerfilUrl: String? = null // foto subida por el usuario
) {
    val nombreEfectivo: String
        get() = nombreMostrado.ifBlank { nombreUsuario }

    val fotoEfectiva: String?
        get() = fotoPerfilUrl ?: fotoUrl
}
