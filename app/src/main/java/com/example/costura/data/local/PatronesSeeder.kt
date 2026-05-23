package com.example.costura.data.local

import com.example.costura.data.local.dao.PatronDao
import com.example.costura.model.PatronComunidad
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object PatronesSeeder {

    private const val COLECCION = "patrones_comunidad"

    suspend fun seedear(dao: PatronDao) {
        val db = FirebaseFirestore.getInstance()
        val patrones = dao.getAllList()

        for (patron in patrones) {
            val docId = "precargado_${slugify(patron.nombre)}"

            val existe = try {
                db.collection(COLECCION).document(docId).get().await().exists()
            } catch (_: Exception) {
                continue
            }

            if (!existe) {
                val doc = PatronComunidad(
                    id = docId,
                    uidAutor = "sistema",
                    nombreAutor = "Costura App",
                    fotoAutorUrl = null,
                    nombre = patron.nombre,
                    categoria = patron.categoria,
                    dificultad = patron.dificultad,
                    anchoCm = patron.anchoCm,
                    largoCm = patron.largoCm,
                    descripcion = patron.descripcion,
                    consejos = "",
                    fotosUrls = patron.imagenAsset
                        .split(",")
                        .filter { it.isNotBlank() },
                    pdfUrl = patron.fuente.ifBlank { null },
                    tutorialUrl = null,
                    likes = 0,
                    fechaPublicacion = Timestamp.now()
                )
                try {
                    db.collection(COLECCION).document(docId).set(doc).await()
                } catch (_: Exception) { }
            }
        }
    }

    private fun slugify(text: String): String = text
        .lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i")
        .replace("ó", "o").replace("ú", "u").replace("ñ", "n").replace("ü", "u")
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
}
