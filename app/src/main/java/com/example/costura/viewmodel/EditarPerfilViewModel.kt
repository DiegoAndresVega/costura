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

    sealed class Estado {
        object Cargando : Estado()
        data class Listo(val usuario: Usuario) : Estado()
        object Guardando : Estado()
        object Exito : Estado()
        object Error : Estado()
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _estado = MutableLiveData<Estado>(Estado.Cargando)
    val estado: LiveData<Estado> = _estado

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        val firebaseUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("usuarios").document(firebaseUser.uid).get().await()
                _estado.value = Estado.Listo(
                    Usuario(
                        uid = firebaseUser.uid,
                        nombreUsuario = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        fotoUrl = firebaseUser.photoUrl?.toString(),
                        nombreMostrado = doc.getString("nombreMostrado") ?: "",
                        bio = doc.getString("bio") ?: "",
                        nivelCostura = doc.getString("nivelCostura") ?: "",
                        fotoPerfilUrl = doc.getString("fotoPerfilUrl")
                    )
                )
            } catch (_: Exception) {
                _estado.value = Estado.Listo(
                    Usuario(
                        uid = firebaseUser.uid,
                        nombreUsuario = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        fotoUrl = firebaseUser.photoUrl?.toString()
                    )
                )
            }
        }
    }

    fun guardar(nombreMostrado: String, bio: String, nivelCostura: String, fotoUri: Uri?) {
        val uid = auth.currentUser?.uid ?: return
        _estado.value = Estado.Guardando
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
                _estado.value = Estado.Exito
            } catch (_: Exception) {
                _estado.value = Estado.Error
            }
        }
    }
}
