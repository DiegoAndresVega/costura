package com.example.costura.ui.subir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.costura.databinding.FragmentSubirBinding
import com.example.costura.viewmodel.SubirViewModel
import com.google.android.material.snackbar.Snackbar

class SubirFragment : Fragment() {

    private var _binding: FragmentSubirBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubirViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFoto.setOnClickListener {
            // TODO: abrir selector de imagen
        }

        binding.btnPublicar.setOnClickListener {
            publicarPatron()
        }

        viewModel.publicado.observe(viewLifecycleOwner) { exito ->
            if (exito) {
                Snackbar.make(binding.root, "Patrón publicado", Snackbar.LENGTH_SHORT).show()
                limpiarFormulario()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun publicarPatron() {
        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val descripcion = binding.etDescripcion.text?.toString()?.trim() ?: ""
        val anchoStr = binding.etAncho.text?.toString()?.trim() ?: ""
        val largoStr = binding.etLargo.text?.toString()?.trim() ?: ""

        if (nombre.isEmpty() || descripcion.isEmpty() || anchoStr.isEmpty() || largoStr.isEmpty()) {
            Snackbar.make(binding.root, com.example.costura.R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show()
            return
        }

        val ancho = anchoStr.toFloatOrNull() ?: return
        val largo = largoStr.toFloatOrNull() ?: return
        val tutorial = binding.etTutorial.text?.toString()?.trim()

        viewModel.publicar(nombre, descripcion, ancho, largo, tutorial)
    }

    private fun limpiarFormulario() {
        binding.etNombre.text?.clear()
        binding.etDescripcion.text?.clear()
        binding.etAncho.text?.clear()
        binding.etLargo.text?.clear()
        binding.etTutorial.text?.clear()
        binding.ivFoto.setImageDrawable(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
