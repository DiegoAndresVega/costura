package com.example.costura.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditarPerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _guardando = MutableLiveData(false)
    val guardando: LiveData<Boolean> = _guardando

    private val _exito = MutableLiveData(false)
    val exito: LiveData<Boolean> = _exito

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        val firebaseUser = auth.currentUser ?: return
        viewModelScope.launch {
            val doc = try {
                db.collection("usuarios").document(firebaseUser.uid).get().await()
            } catch (_: Exception) { null }
            _usuario.value = Usuario.from(firebaseUser, doc)
        }
    }

    fun guardar(nombreMostrado: String, bio: String, nivelCostura: String, fotoUri: Uri?) {
        val uid = auth.currentUser?.uid ?: return
        _guardando.value = true
        viewModelScope.launch {
            try {
                val datos = mutableMapOf<String, Any>(
                    "uid" to uid,
                    "nombreMostrado" to nombreMostrado.trim(),
                    "bio" to bio.trim(),
                    "nivelCostura" to nivelCostura
                )
                if (fotoUri != null) {
                    val ref = storage.reference.child("fotos_perfil/$uid")
                    ref.putFile(fotoUri).await()
                    datos["fotoPerfilUrl"] = ref.downloadUrl.await().toString()
                }
                db.collection("usuarios").document(uid)
                    .set(datos, SetOptions.merge()).await()
                _exito.value = true
            } catch (_: Exception) {
                _error.value = "No se pudo guardar. Inténtalo de nuevo"
            } finally {
                _guardando.value = false
            }
        }
    }
}
