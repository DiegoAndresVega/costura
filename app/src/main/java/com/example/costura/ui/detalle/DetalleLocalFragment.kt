package com.example.costura.ui.detalle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.costura.R
import com.example.costura.databinding.FragmentDetalleLocalBinding
import com.example.costura.ui.common.FotosAdapter
import com.example.costura.viewmodel.DetalleLocalViewModel

class DetalleLocalFragment : Fragment() {

    private var _binding: FragmentDetalleLocalBinding? = null
    private val binding get() = _binding!!

    private val patronId by lazy { arguments?.getInt("patronId") ?: 0 }

    private val viewModel: DetalleLocalViewModel by viewModels {
        DetalleLocalViewModel.Factory(requireActivity().application, patronId)
    }

    private val fotosAdapter = FotosAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleLocalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFotos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFotos.adapter = fotosAdapter

        viewModel.fotos.observe(viewLifecycleOwner) { fotos ->
            fotosAdapter.submitList(fotos)
        }

        viewModel.patron.observe(viewLifecycleOwner) { patron ->
            patron ?: return@observe
            binding.tvNombre.text = patron.nombre
            binding.chipCategoria.text = patron.categoria
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }
            binding.chipDificultad.text = patron.dificultad
                .replaceFirstChar { it.uppercase() }
            binding.tvMedidas.text = getString(
                R.string.detalle_medidas, patron.anchoCm, patron.largoCm
            )
            binding.tvDescripcion.text = patron.descripcion

            if (patron.fuente.isNotBlank()) {
                binding.btnPdf.visibility = View.VISIBLE
                binding.btnPdf.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.fuente)))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
