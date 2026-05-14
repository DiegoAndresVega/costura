package com.example.costura.ui.historial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.costura.databinding.FragmentHistorialBinding
import com.example.costura.viewmodel.HistorialViewModel

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistorialViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        viewModel.historial.observe(viewLifecycleOwner) { entradas ->
            if (entradas.isEmpty()) {
                binding.tvVacio.visibility = View.VISIBLE
                binding.rvHistorial.visibility = View.GONE
            } else {
                binding.tvVacio.visibility = View.GONE
                binding.rvHistorial.visibility = View.VISIBLE
                // TODO: conectar adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
