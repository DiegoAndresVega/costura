package com.example.costura.data.local

import com.example.costura.data.local.dao.PatronDao
import com.example.costura.data.local.entity.PatronLocal

object PatronesPreload {

    suspend fun insertar(dao: PatronDao) {
        if (dao.count() > 0) return

        val fotosRinonera = listOf(
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera1.png?alt=media&token=87dc847e-cf9b-4668-8a1d-13e8214147ca",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera2.png?alt=media&token=7b8879b7-1818-4978-96fd-ddd67f5c19f5",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera3.png?alt=media&token=3a7531ae-cc32-41f6-94b1-00b61b2a181f",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Frinonera4.png?alt=media&token=d04ad7eb-1cc0-4a3f-9c0f-925ca1e75feb"
        ).joinToString(",")

        val fotosBikepacking = listOf(
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Fbikepacking%2FCaptura%20de%20pantalla%202026-05-23%20a%20las%2019.39.54.png?alt=media&token=8587f956-c8c4-4588-8b32-29ec993dd2f8",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Fbikepacking%2FCaptura%20de%20pantalla%202026-05-23%20a%20las%2019.39.58.png?alt=media&token=3e26e0cf-b58b-42e1-a851-07b614771d43",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Fbikepacking%2FCaptura%20de%20pantalla%202026-05-23%20a%20las%2019.40.02.png?alt=media&token=4c0f4def-1aee-473d-a24e-40af3b67cbcf",
            "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Fbikepacking%2FCaptura%20de%20pantalla%202026-05-23%20a%20las%2019.40.07.png?alt=media&token=79386014-7bd4-4f4f-954c-80d87245fdf1"
        ).joinToString(",")

        val patrones = listOf(
            PatronLocal(
                nombre = "Riñonera para uso diario",
                categoria = "costura_basica",
                anchoCm = 71f,
                largoCm = 40f,
                dificultad = "fácil",
                descripcion = "Riñonera de uso diario con compartimento principal, bolsillo interior tipo funda y bolsillo frontal exterior con cremallera para llaves. Diseño apto para principiantes. Resultado final: 22,9 × 12,7 × 7,6 cm.",
                imagenAsset = fotosRinonera,
                fuente = "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/patrones_precargados%2Frinonera_uso_diario_patron.pdf?alt=media&token=5995888b-3104-4d8f-9580-27eb4cfdd6e5"
            ),
            PatronLocal(
                nombre = "Bolsa de manillar para bikepacking",
                categoria = "costura_basica",
                anchoCm = 50f,
                largoCm = 35f,
                dificultad = "medio",
                descripcion = "Bolsa de manillar tipo stem bag para bikepacking. Ideal para llevar snacks, el teléfono o gafas de sol. Resultado final: 10 × 18 cm. Incluye bolsillo exterior de malla y cordón de cierre. Nivel intermedio.",
                imagenAsset = fotosBikepacking,
                fuente = "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/patrones_precargados%2Fbikepacking.pdf?alt=media&token=efe6c463-2953-49f6-9a92-fd58b93e66a6"
            ),
            PatronLocal(
                nombre = "Pantalón de chándal hombre S-XL",
                categoria = "ropa",
                anchoCm = 150f,
                largoCm = 160f,
                dificultad = "medio",
                descripcion = "Patrón de pantalón de chándal para hombre en tallas S a XL. Incluye cinturilla elástica y bajos con puño. Apto para tela de punto o felpa.",
                imagenAsset = "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/fotos_precargadas%2Fpantalon%2FPatro%CC%81n%20pantalo%CC%81n%20chandal%20hombre%20PDF.jpg?alt=media&token=660ee86a-c6d4-43ed-993b-525c62d3ba84",
                fuente = "https://firebasestorage.googleapis.com/v0/b/costura-30e86.firebasestorage.app/o/patrones_precargados%2FChandal%20hombre%20S-XL.pdf?alt=media&token=98279320-b791-429b-99ea-253c075b903e",
                tutorialUrl = "https://www.youtube.com/watch?v=BgOftt5rHfQ"
            )
        )

        dao.insertAll(patrones)
    }
}
