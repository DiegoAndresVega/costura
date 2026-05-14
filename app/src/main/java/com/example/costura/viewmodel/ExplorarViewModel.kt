package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.costura.model.PatronComunidad
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ExplorarViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _patrones = MutableLiveData<List<PatronComunidad>>(emptyList())
    val patrones: LiveData<List<PatronComunidad>> = _patrones

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private var listener: ListenerRegistration? = null

    init {
        escucharPatrones()
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
            _patrones.value = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
