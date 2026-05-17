package com.example.costura.ui.explorar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.costura.R
import com.example.costura.databinding.ItemPatronComunidadBinding
import com.example.costura.model.PatronComunidad

class PatronComunidadAdapter(
    private val onClick: (PatronComunidad) -> Unit,
    private val onGuardar: ((String) -> Unit)? = null
) : ListAdapter<PatronComunidad, PatronComunidadAdapter.ViewHolder>(DIFF) {

    private var savedIds: Set<String> = emptySet()

    fun updateSavedIds(ids: Set<String>) {
        savedIds = ids
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPatronComunidadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patron: PatronComunidad) {
            binding.tvNombre.text = patron.nombre
            binding.tvAutor.text = patron.nombreAutor
            binding.tvMedidas.text = "${patron.anchoCm.toInt()} × ${patron.largoCm.toInt()} cm"
            binding.chipCategoria.text = patron.categoria
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }

            val primeraFoto = patron.fotosUrls.firstOrNull()
            if (!primeraFoto.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(primeraFoto)
                    .placeholder(android.R.color.darker_gray)
                    .into(binding.ivFoto)
            } else {
                binding.ivFoto.setImageResource(android.R.color.darker_gray)
            }

            if (onGuardar != null) {
                binding.btnGuardar.visibility = View.VISIBLE
                val guardado = savedIds.contains(patron.id)
                binding.btnGuardar.setIconResource(
                    if (guardado) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
                )
                binding.btnGuardar.setOnClickListener { onGuardar.invoke(patron.id) }
            } else {
                binding.btnGuardar.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(patron) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPatronComunidadBinding.inflate(
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
