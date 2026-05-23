package com.example.costura.ui.explorar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.costura.R
import com.example.costura.databinding.FragmentExplorarBinding
import com.example.costura.viewmodel.ExplorarViewModel
import com.google.android.material.snackbar.Snackbar

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExplorarViewModel by viewModels()

    private val adapter = PatronComunidadAdapter(
        onClick = { patron ->
            findNavController().navigate(
                R.id.detalleFragment,
                bundleOf("patronId" to patron.id)
            )
        },
        onGuardar = { patronId -> viewModel.toggleGuardado(patronId) }
    )

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

        binding.searchView.setupWithSearchBar(binding.searchBar)
        setupSearch()
        setupCategoryChips()
        observeViewModel()
    }

    private fun setupSearch() {
        binding.searchView.editText.addTextChangedListener { text ->
            val q = text?.toString() ?: ""
            viewModel.setQuery(q)
            binding.searchBar.setText(q)
        }
    }

    private fun setupCategoryChips() {
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { _, checkedIds ->
            val cat = when (checkedIds.firstOrNull()) {
                R.id.chip_ropa -> "ropa"
                R.id.chip_costura_basica -> "costura_basica"
                R.id.chip_hogar -> "hogar"
                R.id.chip_infantil -> "infantil"
                else -> null
            }
            viewModel.escucharPatrones(cat)
        }
    }

    private fun observeViewModel() {
        viewModel.patrones.observe(viewLifecycleOwner) { patrones ->
            adapter.submitList(patrones)
            binding.tvSinResultados.visibility =
                if (patrones.isEmpty() && binding.progress.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }
        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            binding.progress.visibility = if (cargando) View.VISIBLE else View.GONE
            if (!cargando) {
                val patrones = viewModel.patrones.value ?: emptyList()
                binding.tvSinResultados.visibility =
                    if (patrones.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        viewModel.savedIds.observe(viewLifecycleOwner) { ids ->
            adapter.updateSavedIds(ids)
        }
        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
