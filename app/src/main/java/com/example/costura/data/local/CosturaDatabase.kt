package com.example.costura.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.costura.data.local.dao.HistorialDao
import com.example.costura.data.local.entity.HistorialMedicion

@Database(
    entities = [HistorialMedicion::class],
    version = 10,
    exportSchema = false
)
abstract class CosturaDatabase : RoomDatabase() {

    abstract fun historialDao(): HistorialDao

    companion object {
        private var instancia: CosturaDatabase? = null

        fun getInstance(context: Context): CosturaDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(
                    context.applicationContext,
                    CosturaDatabase::class.java,
                    "costura_db"
                ).fallbackToDestructiveMigration(true).build()
            }
            return instancia!!
        }
    }
}
