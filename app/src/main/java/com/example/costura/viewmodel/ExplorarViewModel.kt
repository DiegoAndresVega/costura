package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExplorarViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _allPatrones = MutableLiveData<List<PatronComunidad>>(emptyList())
    private val _query = MutableLiveData("")

    val patrones: MediatorLiveData<List<PatronComunidad>> = MediatorLiveData<List<PatronComunidad>>().apply {
        fun update() {
            val q = _query.value?.trim()?.lowercase() ?: ""
            val all = _allPatrones.value ?: emptyList()
            value = if (q.isEmpty()) all
            else all.filter { p ->
                p.nombre.lowercase().contains(q) ||
                p.nombreAutor.lowercase().contains(q) ||
                p.descripcion.lowercase().contains(q)
            }
        }
        addSource(_allPatrones) { update() }
        addSource(_query) { update() }
    }

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _savedIds = MutableLiveData<Set<String>>(emptySet())
    val savedIds: LiveData<Set<String>> = _savedIds

    private var listener: ListenerRegistration? = null

    init {
        escucharPatrones()
        cargarGuardados()
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun escucharPatrones(categoria: String? = null) {
        listener?.remove()
        _cargando.value = true

        var query: Query = db.collection("patrones_comunidad").limit(50)
        if (!categoria.isNullOrEmpty()) {
            query = db.collection("patrones_comunidad")
                .whereEqualTo("categoria", categoria)
                .limit(50)
        }

        listener = query.addSnapshotListener { snapshot, _ ->
            _cargando.value = false
            snapshot ?: return@addSnapshotListener
            _allPatrones.value = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            }
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
        _savedIds.value = if (eraGuardado) {
            (_savedIds.value ?: emptySet()) - patronId
        } else {
            (_savedIds.value ?: emptySet()) + patronId
        }
        viewModelScope.launch {
            try {
                if (eraGuardado) ref.delete().await()
                else ref.set(mapOf("fechaGuardado" to Timestamp.now())).await()
            } catch (_: Exception) {
                _savedIds.value = if (eraGuardado) {
                    (_savedIds.value ?: emptySet()) + patronId
                } else {
                    (_savedIds.value ?: emptySet()) - patronId
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
