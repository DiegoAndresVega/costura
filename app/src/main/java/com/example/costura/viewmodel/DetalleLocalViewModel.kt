package com.example.costura.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.entity.PatronLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetalleLocalViewModel(application: Application, private val patronId: Int) : AndroidViewModel(application) {

    private val db = CosturaDatabase.getInstance(application)

    private val _patron = MutableLiveData<PatronLocal?>()
    val patron: LiveData<PatronLocal?> = _patron

    private val _fotos = MutableLiveData<List<String>>(emptyList())
    val fotos: LiveData<List<String>> = _fotos

    init {
        cargarPatron()
    }

    private fun cargarPatron() {
        viewModelScope.launch(Dispatchers.IO) {
            val p = db.patronDao().getById(patronId)
            _patron.postValue(p)
            _fotos.postValue(
                p?.imagenAsset?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            )
        }
    }

    class Factory(private val application: Application, private val patronId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleLocalViewModel(application, patronId) as T
    }
}
