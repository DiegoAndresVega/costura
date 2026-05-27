package com.example.costura.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SubirViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _publicado = MutableLiveData(false)
    val publicado: LiveData<Boolean> = _publicado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _fotosUris = MutableLiveData<List<Uri>>(emptyList())
    val fotosUris: LiveData<List<Uri>> = _fotosUris

    private var pdfUri: Uri? = null

    private val _pdfNombre = MutableLiveData<String?>()
    val pdfNombre: LiveData<String?> = _pdfNombre

    fun setFotosUris(uris: List<Uri>) {
        _fotosUris.value = uris.take(4)
    }

    fun setPdfUri(uri: Uri, nombre: String) {
        pdfUri = uri
        _pdfNombre.value = nombre
    }

    fun publicar(
        nombre: String,
        descripcion: String,
        categoria: String,
        dificultad: String,
        anchoCm: Float,
        largoCm: Float,
        consejos: String,
        tutorialUrl: String?
    ) {
        val user = auth.currentUser
        if (user == null) {
            _error.value = "Debes iniciar sesión para publicar"
            return
        }

        _cargando.value = true
        _publicado.value = false
        viewModelScope.launch {
            try {
                val fotosUrls = (_fotosUris.value ?: emptyList()).map { uri ->
                    val ref = storage.reference.child("patrones/${UUID.randomUUID()}.jpg")
                    ref.putFile(uri).await()
                    ref.downloadUrl.await().toString()
                }

                var pdfUrl: String? = null
                pdfUri?.let { uri ->
                    val ref = storage.reference.child("patrones_pdf/${UUID.randomUUID()}.pdf")
                    ref.putFile(uri).await()
                    pdfUrl = ref.downloadUrl.await().toString()
                }

                val patron = PatronComunidad(
                    uidAutor = user.uid,
                    nombreAutor = user.displayName ?: "",
                    fotoAutorUrl = user.photoUrl?.toString(),
                    nombre = nombre,
                    descripcion = descripcion,
                    categoria = categoria,
                    dificultad = dificultad,
                    anchoCm = anchoCm,
                    largoCm = largoCm,
                    consejos = consejos,
                    fotosUrls = fotosUrls,
                    pdfUrl = pdfUrl,
                    tutorialUrl = tutorialUrl?.ifEmpty { null }
                )

                db.collection("patrones_comunidad").add(patron).await()
                _fotosUris.value = emptyList()
                pdfUri = null
                _pdfNombre.value = null
                _publicado.value = true
            } catch (_: Exception) {
                _error.value = "No se pudo publicar el patrón"
            } finally {
                _cargando.value = false
            }
        }
    }
}
