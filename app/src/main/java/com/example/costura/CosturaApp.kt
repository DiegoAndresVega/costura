package com.example.costura

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.PatronesPreload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CosturaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        CoroutineScope(Dispatchers.IO).launch {
            PatronesPreload.insertar(
                CosturaDatabase.getInstance(this@CosturaApp).patronDao(),
                this@CosturaApp
            )
        }
    }
}
