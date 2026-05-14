package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SubirViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _publicado = MutableLiveData(false)
    val publicado: LiveData<Boolean> = _publicado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun publicar(
        nombre: String,
        descripcion: String,
        anchoCm: Float,
        largoCm: Float,
        tutorialUrl: String?
    ) {
        val user = auth.currentUser
        if (user == null) {
            _error.value = "Debes iniciar sesión para publicar"
            return
        }

        viewModelScope.launch {
            try {
                val patron = PatronComunidad(
                    uidAutor = user.uid,
                    nombreAutor = user.displayName ?: "",
                    nombre = nombre,
                    descripcion = descripcion,
                    anchoCm = anchoCm,
                    largoCm = largoCm,
                    tutorialUrl = tutorialUrl?.ifEmpty { null }
                )

                db.collection("patrones_comunidad").add(patron).await()
                _publicado.value = true
                _publicado.value = false
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
