package com.example.costura.ui.historial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.costura.R
import com.example.costura.data.local.entity.HistorialMedicion
import com.example.costura.databinding.ItemHistorialBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistorialAdapter : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    private var lista: List<HistorialMedicion> = emptyList()

    fun actualizar(nuevaLista: List<HistorialMedicion>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicion: HistorialMedicion) {
            binding.tvFecha.text = try {
                FORMATTER.format(Instant.parse(medicion.fecha))
            } catch (_: Exception) {
                medicion.fecha.take(10)
            }
            binding.tvMedidas.text = binding.root.context.getString(
                R.string.historial_tela, medicion.anchoCm, medicion.largoCm
            )
            val num = if (medicion.patronesEncajan.isBlank()) 0
                      else medicion.patronesEncajan.split(",").count { it.isNotBlank() }
            binding.tvPatrones.text = binding.root.context.getString(
                R.string.historial_patrones, num
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
    }
}
