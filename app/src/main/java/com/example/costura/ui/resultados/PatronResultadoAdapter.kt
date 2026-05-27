package com.example.costura.ui.resultados

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.costura.R
import com.example.costura.databinding.ItemPatronResultadoBinding
import com.example.costura.model.PatronComunidad

class PatronResultadoAdapter(
    private val onClick: (PatronComunidad) -> Unit = {}
) : ListAdapter<PatronComunidad, PatronResultadoAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemPatronResultadoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patron: PatronComunidad) {
            binding.tvNombre.text = patron.nombre
            binding.tvCategoria.text = patron.categoria.replace("_", " ").replaceFirstChar { it.uppercase() }
            binding.tvMedidas.text = "Tela mínima: ${patron.anchoCm.toInt()} × ${patron.largoCm.toInt()} cm"
            binding.tvDescripcion.text = patron.descripcion
            binding.tvDificultad.text = patron.dificultad

            val (bgColor, textColor) = when (patron.dificultad) {
                "fácil" -> Pair(R.color.dificultad_facil, R.color.dificultad_facil_text)
                "medio" -> Pair(R.color.dificultad_medio, R.color.dificultad_medio_text)
                else -> Pair(R.color.dificultad_dificil, R.color.dificultad_dificil_text)
            }
            binding.tvDificultad.setChipBackgroundColorResource(bgColor)
            binding.tvDificultad.setTextColor(ContextCompat.getColor(binding.root.context, textColor))

            val primeraFoto = patron.fotosUrls.firstOrNull { it.isNotBlank() }
            if (!primeraFoto.isNullOrEmpty()) {
                binding.ivPortada.visibility = View.VISIBLE
                Glide.with(binding.root).load(primeraFoto).centerCrop().into(binding.ivPortada)
            } else {
                binding.ivPortada.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(patron) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPatronResultadoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PatronComunidad>() {
            override fun areItemsTheSame(a: PatronComunidad, b: PatronComunidad) = a.id == b.id
            override fun areContentsTheSame(a: PatronComunidad, b: PatronComunidad) = a == b
        }
    }
}
