package com.example.costura

import com.example.costura.data.local.CalculoTela
import com.example.costura.model.PatronComunidad
import org.junit.Assert.*
import org.junit.Test

class CalculoTelaTest {

    private fun patron(nombre: String, ancho: Float, largo: Float) = PatronComunidad(
        nombre = nombre,
        categoria = "ropa",
        anchoCm = ancho,
        largoCm = largo,
        dificultad = "fácil",
        descripcion = ""
    )

    @Test
    fun encaja_cuandoDimensionesIgualesALaTela() {
        val p = patron("Exacto", 40f, 30f)
        assertTrue(CalculoTela.encaja(p, 40f, 30f))
    }

    @Test
    fun encaja_cuandoPatronEsMasPequenoQueLaTela() {
        val p = patron("Pequeño", 20f, 15f)
        assertTrue(CalculoTela.encaja(p, 40f, 30f))
    }

    @Test
    fun noEncaja_cuandoAnchoSuperaLaTela() {
        val p = patron("AnchoGrande", 50f, 20f)
        assertFalse(CalculoTela.encaja(p, 40f, 30f))
    }

    @Test
    fun noEncaja_cuandoLargoSuperaLaTela() {
        val p = patron("LargoGrande", 30f, 45f)
        assertFalse(CalculoTela.encaja(p, 40f, 30f))
    }

    @Test
    fun filtrarEncajan_devuelveSoloLosQueEntran() {
        val patrones = listOf(
            patron("Pequeño", 20f, 15f),
            patron("Justo", 40f, 30f),
            patron("Grande", 60f, 50f)
        )
        val resultado = CalculoTela.filtrarEncajan(patrones, 40f, 30f)
        assertEquals(2, resultado.size)
        assertFalse(resultado.any { it.nombre == "Grande" })
    }

    @Test
    fun filtrarNoEncajan_devuelveLosQueNoEntran() {
        val patrones = listOf(
            patron("Pequeño", 20f, 15f),
            patron("Grande", 60f, 50f)
        )
        val resultado = CalculoTela.filtrarNoEncajan(patrones, 40f, 30f)
        assertEquals(1, resultado.size)
        assertEquals("Grande", resultado.first().nombre)
    }
}
