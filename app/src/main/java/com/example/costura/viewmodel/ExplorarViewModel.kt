package com.example.costura.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.costura.model.PatronComunidad
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExplorarViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _patrones = MutableLiveData<List<PatronComunidad>>(emptyList())
    val patrones: LiveData<List<PatronComunidad>> = _patrones

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    init {
        cargarPatrones()
    }

    fun cargarPatrones(categoria: String? = null) {
        _cargando.value = true
        viewModelScope.launch {
            try {
                var query: Query = db.collection("patrones_comunidad")
                    .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                    .limit(50)

                if (!categoria.isNullOrEmpty()) {
                    query = db.collection("patrones_comunidad")
                        .whereEqualTo("categoria", categoria)
                        .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
                        .limit(50)
                }

                val snapshot = query.get().await()
                val lista = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
                }
                _patrones.value = lista
            } catch (_: Exception) {
            } finally {
                _cargando.value = false
            }
        }
    }
}
