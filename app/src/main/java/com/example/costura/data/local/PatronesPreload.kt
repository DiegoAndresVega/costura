package com.example.costura.data.local

import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.PatronLocal

object PatronesPreload {

    suspend fun insertar(dao: PatronDao) {
        if (dao.count() > 0) return

        val patrones = listOf(
            PatronLocal(
                nombre = "Riñonera para uso diario",
                categoria = "costura_basica",
                anchoCm = 71f,
                largoCm = 40f,
                dificultad = "fácil",
                descripcion = "Riñonera de uso diario con compartimento principal, bolsillo interior tipo funda y bolsillo frontal exterior con cremallera para llaves. Diseño apto para principiantes. Resultado final: 22,9 × 12,7 × 7,6 cm.",
                fuente = "https://learnmyog.com"
            )
        )

        dao.insertAll(patrones)
    }
}
