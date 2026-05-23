package com.example.costura.data.local

import com.example.costura.data.local.entity.PatronLocal

object CalculoTela {

    fun encaja(patron: PatronLocal, anchoCm: Float, largoCm: Float): Boolean =
        patron.anchoCm <= anchoCm && patron.largoCm <= largoCm

    fun filtrarEncajan(patrones: List<PatronLocal>, anchoCm: Float, largoCm: Float): List<PatronLocal> =
        patrones.filter { encaja(it, anchoCm, largoCm) }

    fun filtrarNoEncajan(patrones: List<PatronLocal>, anchoCm: Float, largoCm: Float): List<PatronLocal> =
        patrones.filterNot { encaja(it, anchoCm, largoCm) }
}
