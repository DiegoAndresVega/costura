package com.example.costura.data.local

import com.example.costura.model.PatronComunidad

object CalculoTela {

    fun encaja(patron: PatronComunidad, anchoCm: Float, largoCm: Float): Boolean =
        patron.anchoCm <= anchoCm && patron.largoCm <= largoCm

    fun filtrarEncajan(patrones: List<PatronComunidad>, anchoCm: Float, largoCm: Float): List<PatronComunidad> =
        patrones.filter { encaja(it, anchoCm, largoCm) }

    fun filtrarNoEncajan(patrones: List<PatronComunidad>, anchoCm: Float, largoCm: Float): List<PatronComunidad> =
        patrones.filterNot { encaja(it, anchoCm, largoCm) }
}
