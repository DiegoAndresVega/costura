package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.costura.model.Comentario
import com.example.costura.model.PatronComunidad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetalleViewModel(private val patronId: String) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _patron = MutableLiveData<PatronComunidad?>()
    val patron: LiveData<PatronComunidad?> = _patron

    private val _comentarios = MutableLiveData<List<Comentario>>(emptyList())
    val comentarios: LiveData<List<Comentario>> = _comentarios

    private val _liked = MutableLiveData(false)
    val liked: LiveData<Boolean> = _liked

    init {
        cargarPatron()
        cargarComentarios()
    }

    private fun cargarPatron() {
        viewModelScope.launch {
            try {
                val doc = db.collection("patrones_comunidad").document(patronId).get().await()
                _patron.value = doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            } catch (_: Exception) {
            }
        }
    }

    private fun cargarComentarios() {
        db.collection("patrones_comunidad")
            .document(patronId)
            .collection("comentarios")
            .orderBy("fecha")
            .addSnapshotListener { snapshot, _ ->
                snapshot ?: return@addSnapshotListener
                _comentarios.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Comentario::class.java)?.copy(id = doc.id)
                }
            }
    }

    fun toggleLike() {
        val user = auth.currentUser ?: return
        val ref = db.collection("patrones_comunidad").document(patronId)
        val delta = if (_liked.value == true) -1L else 1L
        _liked.value = !(_liked.value ?: false)
        viewModelScope.launch {
            try {
                ref.update("likes", FieldValue.increment(delta)).await()
            } catch (_: Exception) {
                _liked.value = !(_liked.value ?: false)
            }
        }
    }

    fun enviarComentario(texto: String) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val comentario = mapOf(
                    "uidAutor" to user.uid,
                    "nombreUsuario" to (user.displayName ?: ""),
                    "texto" to texto,
                    "fecha" to com.google.firebase.Timestamp.now()
                )
                db.collection("patrones_comunidad")
                    .document(patronId)
                    .collection("comentarios")
                    .add(comentario)
                    .await()
            } catch (_: Exception) {
            }
        }
    }

    class Factory(private val patronId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleViewModel(patronId) as T
    }
}
