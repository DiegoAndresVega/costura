package com.example.costura

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class CosturaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
