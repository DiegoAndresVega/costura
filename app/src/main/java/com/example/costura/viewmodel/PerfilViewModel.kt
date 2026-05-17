package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.example.costura.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _misPatrones = MutableLiveData<List<PatronComunidad>>(emptyList())
    val misPatrones: LiveData<List<PatronComunidad>> = _misPatrones

    private val _guardados = MutableLiveData<List<PatronComunidad>>(emptyList())
    val guardados: LiveData<List<PatronComunidad>> = _guardados

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
        cargarMisPatrones(firebaseUser.uid)
        cargarGuardados(firebaseUser.uid)
    }

    private fun cargarMisPatrones(uid: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("patrones_comunidad")
                    .whereEqualTo("uidAutor", uid)
                    .get()
                    .await()
                _misPatrones.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
                }
            } catch (_: Exception) { }
        }
    }

    private fun cargarGuardados(uid: String) {
        viewModelScope.launch {
            try {
                val guardadosSnapshot = db.collection("usuarios").document(uid)
                    .collection("guardados").get().await()
                val ids = guardadosSnapshot.documents.map { it.id }
                if (ids.isEmpty()) { _guardados.value = emptyList(); return@launch }
                val patronesSnapshot = db.collection("patrones_comunidad")
                    .whereIn(FieldPath.documentId(), ids)
                    .get().await()
                _guardados.value = patronesSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
                }
            } catch (_: Exception) { }
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        _usuario.value = null
    }
}
