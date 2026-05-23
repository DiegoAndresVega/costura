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

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        cargarUsuario()
    }

    fun recargar() {
        cargarUsuario()
    }

    private fun cargarUsuario() {
        val firebaseUser = auth.currentUser ?: return
        // Datos inmediatos desde Auth mientras carga Firestore
        _usuario.value = Usuario(
            uid = firebaseUser.uid,
            nombreUsuario = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            fotoUrl = firebaseUser.photoUrl?.toString()
        )
        viewModelScope.launch {
            try {
                val doc = db.collection("usuarios").document(firebaseUser.uid).get().await()
                _usuario.value = Usuario(
                    uid = firebaseUser.uid,
                    nombreUsuario = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    fotoUrl = firebaseUser.photoUrl?.toString(),
                    nombreMostrado = doc.getString("nombreMostrado") ?: "",
                    bio = doc.getString("bio") ?: "",
                    nivelCostura = doc.getString("nivelCostura") ?: "",
                    fotoPerfilUrl = doc.getString("fotoPerfilUrl")
                )
            } catch (_: Exception) {
                _error.value = "No se pudo cargar el perfil"
            }

            cargarMisPatrones(firebaseUser.uid)
            cargarGuardados(firebaseUser.uid)
        }
    }

    private suspend fun cargarMisPatrones(uid: String) {
        try {
            val snapshot = db.collection("patrones_comunidad")
                .whereEqualTo("uidAutor", uid)
                .get().await()
            _misPatrones.value = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            _error.value = "Error al cargar tus patrones"
        }
    }

    private suspend fun cargarGuardados(uid: String) {
        try {
            val guardadosSnapshot = db.collection("usuarios").document(uid)
                .collection("guardados").get().await()
            val ids = guardadosSnapshot.documents.map { it.id }
            if (ids.isEmpty()) { _guardados.value = emptyList(); return }
            val patronesSnapshot = db.collection("patrones_comunidad")
                .whereIn(FieldPath.documentId(), ids).get().await()
            _guardados.value = patronesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            _error.value = "Error al cargar los patrones guardados"
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        _usuario.value = null
    }
}
