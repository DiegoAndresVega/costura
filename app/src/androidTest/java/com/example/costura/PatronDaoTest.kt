package com.example.costura

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.costura.data.local.CosturaDatabase
import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.PatronLocal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PatronDaoTest {

    private lateinit var db: CosturaDatabase
    private lateinit var dao: PatronDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CosturaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.patronDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun patron(nombre: String, ancho: Float, largo: Float) = PatronLocal(
        nombre = nombre,
        categoria = "ropa",
        anchoCm = ancho,
        largoCm = largo,
        dificultad = "fácil",
        descripcion = "desc"
    )

    @Test
    fun insertAll_yCount_devuelveNumeroCorrectoDeFilas() = runBlocking {
        dao.insertAll(listOf(patron("A", 30f, 20f), patron("B", 50f, 40f)))
        assertEquals(2, dao.count())
    }

    @Test
    fun getQueEncajan_devuelvePatronesConDimensionMenorOIgual() = runBlocking {
        dao.insertAll(listOf(
            patron("Pequeño", 20f, 15f),
            patron("Justo", 40f, 30f),
            patron("Grande", 60f, 50f)
        ))
        val resultado = dao.getQueEncajan(40f, 30f).first()
        assertEquals(2, resultado.size)
        assertFalse(resultado.any { it.nombre == "Grande" })
    }

    @Test
    fun getQueNoEncajan_devuelvePatronesQueExceden() = runBlocking {
        dao.insertAll(listOf(
            patron("Pequeño", 20f, 15f),
            patron("Grande", 60f, 50f)
        ))
        val resultado = dao.getQueNoEncajan(40f, 30f).first()
        assertEquals(1, resultado.size)
        assertEquals("Grande", resultado.first().nombre)
    }

    @Test
    fun getAllList_devuelveOrdenadosPorNombreAscendente() = runBlocking {
        dao.insertAll(listOf(patron("Zebra", 30f, 20f), patron("Alpha", 20f, 15f)))
        val resultado = dao.getAllList()
        assertEquals("Alpha", resultado.first().nombre)
        assertEquals("Zebra", resultado.last().nombre)
    }
}
