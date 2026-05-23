package com.example.costura.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.costura.data.local.dao.HistorialDao
import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.HistorialMedicion
import com.example.costura.data.local.entity.PatronLocal

@Database(
    entities = [PatronLocal::class, HistorialMedicion::class],
    version = 9,
    exportSchema = false
)
abstract class CosturaDatabase : RoomDatabase() {

    abstract fun patronDao(): PatronDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var INSTANCE: CosturaDatabase? = null

        fun getInstance(context: Context): CosturaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CosturaDatabase::class.java,
                    "costura_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
