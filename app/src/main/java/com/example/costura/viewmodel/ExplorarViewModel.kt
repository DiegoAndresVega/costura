package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExplorarViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var todosLosPatrones: List<PatronComunidad> = emptyList()

    val patrones = MutableLiveData<List<PatronComunidad>>(emptyList())

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _savedIds = MutableLiveData<Set<String>>(emptySet())
    val savedIds: LiveData<Set<String>> = _savedIds

    init {
        escucharPatrones()
        cargarGuardados()
    }

    fun setQuery(q: String) {
        val texto = q.trim().lowercase()
        patrones.value = if (texto.isEmpty()) todosLosPatrones
        else todosLosPatrones.filter { p ->
            p.nombre.lowercase().contains(texto) ||
            p.nombreAutor.lowercase().contains(texto) ||
            p.descripcion.lowercase().contains(texto)
        }
    }

    fun escucharPatrones(categoria: String? = null) {
        _cargando.value = true

        var query: Query = db.collection("patrones_comunidad").limit(50)
        if (!categoria.isNullOrEmpty()) {
            query = db.collection("patrones_comunidad")
                .whereEqualTo("categoria", categoria)
                .limit(50)
        }

        query.addSnapshotListener { snapshot, err ->
            _cargando.value = false
            if (err != null) { _error.value = "Error al cargar los patrones"; return@addSnapshotListener }
            snapshot ?: return@addSnapshotListener
            todosLosPatrones = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            }
            patrones.value = todosLosPatrones
        }
    }

    private fun cargarGuardados() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("usuarios").document(uid)
                    .collection("guardados").get().await()
                _savedIds.value = snapshot.documents.map { it.id }.toSet()
            } catch (_: Exception) { }
        }
    }

    fun toggleGuardado(patronId: String) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.collection("usuarios").document(uid)
            .collection("guardados").document(patronId)
        val eraGuardado = _savedIds.value?.contains(patronId) == true
        viewModelScope.launch {
            try {
                if (eraGuardado) ref.delete().await()
                else ref.set(mapOf("fechaGuardado" to Timestamp.now())).await()
                _savedIds.value = if (eraGuardado) {
                    (_savedIds.value ?: emptySet()) - patronId
                } else {
                    (_savedIds.value ?: emptySet()) + patronId
                }
            } catch (_: Exception) {
                _error.value = "Error al guardar el patrón"
            }
        }
    }

}
