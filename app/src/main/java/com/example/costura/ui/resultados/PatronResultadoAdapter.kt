package com.example.costura.ui.resultados

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.costura.data.local.entity.PatronLocal
import com.example.costura.databinding.ItemPatronResultadoBinding

class PatronResultadoAdapter(
    private val onClick: (PatronLocal) -> Unit = {}
) : ListAdapter<PatronLocal, PatronResultadoAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemPatronResultadoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patron: PatronLocal) {
            binding.tvNombre.text = patron.nombre
            binding.tvCategoria.text = patron.categoria.replace("_", " ").replaceFirstChar { it.uppercase() }
            binding.tvMedidas.text = "Tela mínima: ${patron.anchoCm.toInt()} × ${patron.largoCm.toInt()} cm"
            binding.tvDescripcion.text = patron.descripcion
            binding.tvDificultad.text = patron.dificultad

            binding.tvDificultad.setChipBackgroundColorResource(
                when (patron.dificultad) {
                    "fácil" -> R.color.dificultad_facil
                    "medio" -> R.color.dificultad_medio
                    else -> R.color.dificultad_dificil
                }
            )

            val primeraFoto = patron.imagenAsset.split(",").firstOrNull { it.isNotBlank() }
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
        private val DIFF = object : DiffUtil.ItemCallback<PatronLocal>() {
            override fun areItemsTheSame(a: PatronLocal, b: PatronLocal) = a.id == b.id
            override fun areContentsTheSame(a: PatronLocal, b: PatronLocal) = a == b
        }
    }
}
