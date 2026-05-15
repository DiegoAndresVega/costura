package com.example.costura.ui.resultados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.costura.R
import com.example.costura.data.local.entity.PatronLocal
import com.example.costura.databinding.FragmentResultadosBinding
import com.example.costura.viewmodel.ResultadosViewModel

class ResultadosFragment : Fragment() {

    private var _binding: FragmentResultadosBinding? = null
    private val binding get() = _binding!!

    private val anchoCm by lazy { arguments?.getFloat("anchoCm") ?: 0f }
    private val largoCm by lazy { arguments?.getFloat("largoCm") ?: 0f }

    private val viewModel: ResultadosViewModel by viewModels {
        ResultadosViewModel.Factory(requireActivity().application, anchoCm, largoCm)
    }

    private val navegarADetalle = { patron: PatronLocal ->
        findNavController().navigate(
            R.id.action_resultados_to_detalle_local,
            bundleOf("patronId" to patron.id)
        )
    }

    private val adapterEncajan = PatronResultadoAdapter(navegarADetalle)
    private val adapterNoEncajan = PatronResultadoAdapter(navegarADetalle)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEncajan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEncajan.adapter = adapterEncajan

        binding.rvNoEncajan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNoEncajan.adapter = adapterNoEncajan

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.resultados.observe(viewLifecycleOwner) { encajan ->
            adapterEncajan.submitList(encajan)
            val hayEncajan = encajan.isNotEmpty()
            binding.tvTituloEncajan.visibility = if (hayEncajan) View.VISIBLE else View.GONE
            binding.rvEncajan.visibility = if (hayEncajan) View.VISIBLE else View.GONE
            actualizarEstadoVacio()
        }

        viewModel.noEncajan.observe(viewLifecycleOwner) { noEncajan ->
            adapterNoEncajan.submitList(noEncajan)
            val hayNoEncajan = noEncajan.isNotEmpty()
            binding.tvTituloNoEncajan.visibility = if (hayNoEncajan) View.VISIBLE else View.GONE
            binding.rvNoEncajan.visibility = if (hayNoEncajan) View.VISIBLE else View.GONE
            actualizarEstadoVacio()
        }
    }

    private fun actualizarEstadoVacio() {
        val encajan = viewModel.resultados.value
        val noEncajan = viewModel.noEncajan.value
        val todoVacio = encajan.isNullOrEmpty() && noEncajan.isNullOrEmpty()
        binding.tvVacio.visibility = if (todoVacio) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
