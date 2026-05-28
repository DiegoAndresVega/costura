package com.example.costura.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.costura.databinding.ItemFotoGaleriaBinding

class FotosAdapter : RecyclerView.Adapter<FotosAdapter.ViewHolder>() {

    private var lista: List<String> = emptyList()

    fun actualizar(nuevaLista: List<String>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemFotoGaleriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            Glide.with(binding.root).load(url).centerCrop().into(binding.ivFoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFotoGaleriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size
}
