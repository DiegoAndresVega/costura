package com.example.costura.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

data class Usuario(
    val uid: String = "",
    val nombreUsuario: String = "",
    val email: String = "",
    val fotoUrl: String? = null,
    val nombreMostrado: String = "",
    val bio: String = "",
    val nivelCostura: String = "",
    val fotoPerfilUrl: String? = null
) {
    val nombreEfectivo: String get() = nombreMostrado.ifBlank { nombreUsuario }
    val fotoEfectiva: String? get() = fotoPerfilUrl ?: fotoUrl

    companion object {
        fun from(user: FirebaseUser, doc: DocumentSnapshot? = null) = Usuario(
            uid = user.uid,
            nombreUsuario = user.displayName ?: "",
            email = user.email ?: "",
            fotoUrl = user.photoUrl?.toString(),
            nombreMostrado = doc?.getString("nombreMostrado") ?: "",
            bio = doc?.getString("bio") ?: "",
            nivelCostura = doc?.getString("nivelCostura") ?: "",
            fotoPerfilUrl = doc?.getString("fotoPerfilUrl")
        )
    }
}
