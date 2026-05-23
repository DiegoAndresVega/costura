package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditarPatronViewModel(private val patronId: String) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _patron = MutableLiveData<PatronComunidad?>()
    val patron: LiveData<PatronComunidad?> = _patron

    private val _guardado = MutableLiveData(false)
    val guardado: LiveData<Boolean> = _guardado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    init {
        cargarPatron()
    }

    private fun cargarPatron() {
        viewModelScope.launch {
            try {
                val doc = db.collection("patrones_comunidad").document(patronId).get().await()
                _patron.value = doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            } catch (_: Exception) {
                _error.value = "No se pudo cargar el patrón"
            }
        }
    }

    fun actualizar(
        nombre: String,
        descripcion: String,
        categoria: String,
        dificultad: String,
        anchoCm: Float,
        largoCm: Float,
        consejos: String,
        tutorialUrl: String?
    ) {
        _cargando.value = true
        viewModelScope.launch {
            try {
                val cambios = mapOf(
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "categoria" to categoria,
                    "dificultad" to dificultad,
                    "anchoCm" to anchoCm,
                    "largoCm" to largoCm,
                    "consejos" to consejos,
                    "tutorialUrl" to (tutorialUrl?.ifEmpty { null })
                )
                db.collection("patrones_comunidad").document(patronId)
                    .update(cambios)
                    .await()
                _guardado.value = true
            } catch (_: Exception) {
                _error.value = "No se pudo guardar. Inténtalo de nuevo"
            } finally {
                _cargando.value = false
            }
        }
    }

    class Factory(private val patronId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EditarPatronViewModel(patronId) as T
    }
}
