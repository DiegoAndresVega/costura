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

    private val _guardado = MutableLiveData(false)
    val guardado: LiveData<Boolean> = _guardado

    private val _esAutor = MutableLiveData(false)
    val esAutor: LiveData<Boolean> = _esAutor

    private val _eliminado = MutableLiveData(false)
    val eliminado: LiveData<Boolean> = _eliminado

    init {
        cargarPatron()
        cargarComentarios()
        cargarEstadoLike()
        cargarEstadoGuardado()
    }

    private fun cargarEstadoLike() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("patrones_comunidad")
                    .document(patronId)
                    .collection("likes")
                    .document(uid)
                    .get()
                    .await()
                _liked.value = doc.exists()
            } catch (_: Exception) { }
        }
    }

    private fun cargarPatron() {
        viewModelScope.launch {
            try {
                val doc = db.collection("patrones_comunidad").document(patronId).get().await()
                val patron = doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
                _patron.value = patron
                _esAutor.value = patron?.uidAutor == auth.currentUser?.uid
            } catch (_: Exception) { }
        }
    }

    fun eliminar() {
        viewModelScope.launch {
            try {
                db.collection("patrones_comunidad").document(patronId).delete().await()
                _eliminado.value = true
            } catch (_: Exception) { }
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

    private fun cargarEstadoGuardado() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("usuarios").document(uid)
                    .collection("guardados").document(patronId).get().await()
                _guardado.value = doc.exists()
            } catch (_: Exception) { }
        }
    }

    fun toggleGuardado() {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.collection("usuarios").document(uid)
            .collection("guardados").document(patronId)
        val eraGuardado = _guardado.value == true
        _guardado.value = !eraGuardado
        viewModelScope.launch {
            try {
                if (eraGuardado) ref.delete().await()
                else ref.set(mapOf("fechaGuardado" to com.google.firebase.Timestamp.now())).await()
            } catch (_: Exception) {
                _guardado.value = eraGuardado
            }
        }
    }

    fun toggleLike() {
        val user = auth.currentUser ?: return
        val patronRef = db.collection("patrones_comunidad").document(patronId)
        val likeRef = patronRef.collection("likes").document(user.uid)
        val eraLiked = _liked.value == true
        _liked.value = !eraLiked
        viewModelScope.launch {
            try {
                if (eraLiked) likeRef.delete().await()
                else likeRef.set(mapOf("uid" to user.uid)).await()
                patronRef.update("likes", FieldValue.increment(if (eraLiked) -1L else 1L)).await()
            } catch (_: Exception) {
                _liked.value = eraLiked
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
