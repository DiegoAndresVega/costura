package com.example.costura.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.costura.data.local.CalculoTela
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.entity.HistorialMedicion
import com.example.costura.model.PatronComunidad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant

class ResultadosViewModel(
    application: Application,
    val anchoCm: Float,
    val largoCm: Float
) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val historialDao = CosturaDatabase.getInstance(application).historialDao()

    private val _resultados = MutableLiveData<List<PatronComunidad>>(emptyList())
    val resultados: LiveData<List<PatronComunidad>> = _resultados

    private val _noEncajan = MutableLiveData<List<PatronComunidad>>(emptyList())
    val noEncajan: LiveData<List<PatronComunidad>> = _noEncajan

    private val _cargando = MutableLiveData(true)
    val cargando: LiveData<Boolean> = _cargando

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        calcular()
    }

    private fun calcular() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("patrones_comunidad").get().await()
                val patrones = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PatronComunidad::class.java)?.copy(id = doc.id)
                }
                val encajan = CalculoTela.filtrarEncajan(patrones, anchoCm, largoCm)
                _resultados.value = encajan
                _noEncajan.value = CalculoTela.filtrarNoEncajan(patrones, anchoCm, largoCm)
                guardarHistorial(encajan)
            } catch (_: Exception) {
                _error.value = "No se pudieron cargar los patrones"
            } finally {
                _cargando.value = false
            }
        }
    }

    private suspend fun guardarHistorial(encajan: List<PatronComunidad>) {
        withContext(Dispatchers.IO) {
            historialDao.insert(
                HistorialMedicion(
                    anchoCm = anchoCm,
                    largoCm = largoCm,
                    fecha = Instant.now().toString(),
                    patronesEncajan = encajan.joinToString(",") { it.id }
                )
            )
        }
    }

    class Factory(
        private val application: Application,
        private val anchoCm: Float,
        private val largoCm: Float
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ResultadosViewModel(application, anchoCm, largoCm) as T
    }
}
