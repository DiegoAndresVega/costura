package com.example.costura.ui.calcular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.costura.R
import com.example.costura.databinding.FragmentCalcularBinding
import com.google.android.material.snackbar.Snackbar

class CalcularFragment : Fragment() {

    private var _binding: FragmentCalcularBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalcularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCalcular.setOnClickListener {
            val anchoStr = binding.etAncho.text?.toString()?.trim()
            val largoStr = binding.etLargo.text?.toString()?.trim()

            if (anchoStr.isNullOrEmpty() || largoStr.isNullOrEmpty()) {
                Snackbar.make(binding.root, R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ancho = anchoStr.toFloatOrNull()
            val largo = largoStr.toFloatOrNull()

            if (ancho == null || largo == null || ancho <= 0 || largo <= 0) {
                Snackbar.make(binding.root, R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findNavController().navigate(
                R.id.action_calcular_to_resultados,
                bundleOf("anchoCm" to ancho, "largoCm" to largo)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
