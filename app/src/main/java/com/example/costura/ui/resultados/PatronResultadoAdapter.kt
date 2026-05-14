package com.example.costura.ui.resultados

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.costura.data.local.entity.PatronLocal
import com.example.costura.databinding.ItemPatronResultadoBinding

class PatronResultadoAdapter : ListAdapter<PatronLocal, PatronResultadoAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemPatronResultadoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patron: PatronLocal) {
            binding.tvNombre.text = patron.nombre
            binding.tvCategoria.text = patron.categoria.replace("_", " ").replaceFirstChar { it.uppercase() }
            binding.tvMedidas.text = "Tela mínima: ${patron.anchoCm.toInt()} × ${patron.largoCm.toInt()} cm"
            binding.tvDescripcion.text = patron.descripcion
            binding.tvDificultad.text = patron.dificultad

            val colorRes = when (patron.dificultad) {
                "fácil" -> com.google.android.material.R.attr.colorTertiaryContainer
                "medio" -> com.google.android.material.R.attr.colorSecondaryContainer
                else -> com.google.android.material.R.attr.colorErrorContainer
            }
            binding.tvDificultad.setChipBackgroundColorResource(
                when (patron.dificultad) {
                    "fácil" -> android.R.color.holo_green_light
                    "medio" -> android.R.color.holo_orange_light
                    else -> android.R.color.holo_red_light
                }
            )
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
        private val DIFF = object : DiffUtil.ItemCallback<PatronLocal>() {
            override fun areItemsTheSame(a: PatronLocal, b: PatronLocal) = a.id == b.id
            override fun areContentsTheSame(a: PatronLocal, b: PatronLocal) = a == b
        }
    }
}
