package com.example.costura.ui.detalle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.costura.databinding.ItemComentarioBinding
import com.example.costura.model.Comentario

class ComentarioAdapter : ListAdapter<Comentario, ComentarioAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemComentarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: Comentario) {
            binding.tvNombreUsuario.text = comentario.nombreUsuario
            binding.tvTexto.text = comentario.texto
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComentarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Comentario>() {
            override fun areItemsTheSame(a: Comentario, b: Comentario) = a.id == b.id
            override fun areContentsTheSame(a: Comentario, b: Comentario) = a == b
        }
    }
}
