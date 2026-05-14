package com.example.costura.ui.explorar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.costura.R
import com.example.costura.databinding.FragmentExplorarBinding
import com.example.costura.viewmodel.ExplorarViewModel

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExplorarViewModel by viewModels()

    private val adapter = PatronComunidadAdapter { patron ->
        findNavController().navigate(
            R.id.detalleFragment,
            bundleOf("patronId" to patron.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPatrones.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPatrones.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.patrones.observe(viewLifecycleOwner) { patrones ->
            adapter.submitList(patrones)
        }
        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            binding.progress.visibility = if (cargando) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
