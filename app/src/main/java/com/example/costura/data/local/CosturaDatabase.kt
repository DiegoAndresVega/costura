package com.example.costura.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.costura.data.local.dao.HistorialDao
import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.HistorialMedicion
import com.example.costura.data.local.entity.PatronLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PatronLocal::class, HistorialMedicion::class],
    version = 8,
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
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): CosturaDatabase {
            var db: CosturaDatabase? = null
            val callback = object : Callback() {
                override fun onCreate(sqLiteDatabase: SupportSQLiteDatabase) {
                    super.onCreate(sqLiteDatabase)
                    db?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            PatronesPreload.insertar(database.patronDao())
                        }
                    }
                }
                override fun onDestructiveMigration(sqLiteDatabase: SupportSQLiteDatabase) {
                    super.onDestructiveMigration(sqLiteDatabase)
                    db?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            PatronesPreload.insertar(database.patronDao())
                        }
                    }
                }
            }
            return Room.databaseBuilder(
                context.applicationContext,
                CosturaDatabase::class.java,
                "costura_db"
            )
                .addCallback(callback)
                .fallbackToDestructiveMigration(true)
                .build()
                .also { db = it }
        }
    }
}
