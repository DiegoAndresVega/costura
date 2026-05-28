package com.example.costura.ui.detalle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.costura.databinding.ItemComentarioBinding
import com.example.costura.model.Comentario

class ComentarioAdapter(
    private val currentUid: String,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    private var lista: List<Comentario> = emptyList()

    fun actualizar(nuevaLista: List<Comentario>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemComentarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: Comentario) {
            binding.tvNombreUsuario.text = comentario.nombreUsuario
            binding.tvTexto.text = comentario.texto
            if (comentario.uidAutor == currentUid) {
                binding.btnEliminarComentario.visibility = View.VISIBLE
                binding.btnEliminarComentario.setOnClickListener { onDelete(comentario.id) }
            } else {
                binding.btnEliminarComentario.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComentarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size
}
