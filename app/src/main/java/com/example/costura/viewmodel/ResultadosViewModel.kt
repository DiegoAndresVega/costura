package com.example.costura.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.entity.HistorialMedicion
import com.example.costura.data.local.entity.PatronLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class ResultadosViewModel(
    application: Application,
    val anchoCm: Float,
    val largoCm: Float
) : AndroidViewModel(application) {

    private val db by lazy {
        CosturaDatabase.getInstance(application)
    }

    val resultados: LiveData<List<PatronLocal>> =
        CosturaDatabase.getInstance(application)
            .patronDao().getQueEncajan(anchoCm, largoCm)
            .asLiveData(Dispatchers.IO)

    val noEncajan: LiveData<List<PatronLocal>> =
        CosturaDatabase.getInstance(application)
            .patronDao().getQueNoEncajan(anchoCm, largoCm)
            .asLiveData(Dispatchers.IO)

    init {
        guardarHistorial()
    }

    private fun guardarHistorial() {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = CosturaDatabase.getInstance(getApplication())
                .patronDao().getQueEncajan(anchoCm, largoCm)
                .first()
                .map { it.id.toString() }

            CosturaDatabase.getInstance(getApplication()).historialDao().insert(
                HistorialMedicion(
                    anchoCm = anchoCm,
                    largoCm = largoCm,
                    fecha = Instant.now().toString(),
                    patronesEncajan = ids.joinToString(",")
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
