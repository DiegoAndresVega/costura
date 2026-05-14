package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.costura.model.Usuario
import com.google.firebase.auth.FirebaseAuth

class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    init {
        cargarUsuario()
    }

    private fun cargarUsuario() {
        val firebaseUser = auth.currentUser ?: return
        _usuario.value = Usuario(
            uid = firebaseUser.uid,
            nombreUsuario = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            fotoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    fun cerrarSesion() {
        auth.signOut()
        _usuario.value = null
    }
}
