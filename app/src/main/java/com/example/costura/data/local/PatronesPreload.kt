package com.example.costura.data.local

import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.PatronLocal

object PatronesPreload {

    suspend fun insertar(dao: PatronDao) {
        if (dao.count() > 0) return

        val fotos = listOf(
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera1.png?alt=media&token=87dc847e-cf9b-4668-8a1d-13e8214147ca",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera2.png?alt=media&token=7b8879b7-1818-4978-96fd-ddd67f5c19f5",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera3.png?alt=media&token=3a7531ae-cc32-41f6-94b1-00b61b2a181f",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera4.png?alt=media&token=d04ad7eb-1cc0-4a3f-9c0f-925ca1e75feb"
        ).joinToString(",")

        val patrones = listOf(
            PatronLocal(
                nombre = "Riñonera para uso diario",
                categoria = "costura_basica",
                anchoCm = 71f,
                largoCm = 40f,
                dificultad = "fácil",
                descripcion = "Riñonera de uso diario con compartimento principal, bolsillo interior tipo funda y bolsillo frontal exterior con cremallera para llaves. Diseño apto para principiantes. Resultado final: 22,9 × 12,7 × 7,6 cm.",
                imagenAsset = fotos,
                fuente = "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/patrones_precargados%2Frinonera_uso_diario_patron.pdf?alt=media&token=5995888b-3104-4d8f-9580-27eb4cfdd6e5"
            ),
            PatronLocal(
                nombre = "Bolsa de manillar para bikepacking",
                categoria = "costura_basica",
                anchoCm = 50f,
                largoCm = 35f,
                dificultad = "medio",
                descripcion = "Bolsa de manillar tipo stem bag para bikepacking. Ideal para llevar snacks, el teléfono o gafas de sol. Resultado final: 10 × 18 cm. Incluye bolsillo exterior de malla y cordón de cierre. Nivel intermedio.",
                imagenAsset = "",
                fuente = ""
            )
        )

        dao.insertAll(patrones)
    }
}
