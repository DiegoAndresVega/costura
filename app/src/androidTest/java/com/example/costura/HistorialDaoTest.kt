package com.example.costura

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.dao.HistorialDao
import com.example.costura.data.local.entity.HistorialMedicion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistorialDaoTest {

    private lateinit var db: CosturaDatabase
    private lateinit var dao: HistorialDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CosturaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.historialDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun medicion(fecha: String, ancho: Float = 40f, largo: Float = 30f) =
        HistorialMedicion(anchoCm = ancho, largoCm = largo, fecha = fecha, patronesEncajan = "")

    @Test
    fun insert_yGetAll_devuelveLaMedicionInsertada() = runBlocking {
        dao.insert(medicion("2026-05-26T10:00:00Z"))
        val lista = dao.getAll().first()
        assertEquals(1, lista.size)
        assertEquals(40f, lista.first().anchoCm)
    }

    @Test
    fun getAll_ordenaPorFechaDescendente() = runBlocking {
        dao.insert(medicion("2026-05-26T10:00:00Z"))
        dao.insert(medicion("2026-05-26T12:00:00Z"))
        val lista = dao.getAll().first()
        assertEquals(2, lista.size)
        assertEquals("2026-05-26T12:00:00Z", lista.first().fecha)
    }
}
