package com.example.costura.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.costura.data.local.CosturaDatabase

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CosturaDatabase.getInstance(application)

    val historial = db.historialDao().getAll().asLiveData()
}
