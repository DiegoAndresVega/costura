package com.example.costura.ui.detalle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.costura.databinding.FragmentDetalleBinding
import com.example.costura.viewmodel.DetalleViewModel

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val patronId by lazy { arguments?.getString("patronId") ?: "" }

    private val viewModel: DetalleViewModel by viewModels {
        DetalleViewModel.Factory(patronId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvComentarios.layoutManager = LinearLayoutManager(requireContext())

        viewModel.patron.observe(viewLifecycleOwner) { patron ->
            patron ?: return@observe
            binding.tvNombre.text = patron.nombre
            binding.tvDescripcion.text = patron.descripcion
            binding.tvMedidas.text = getString(
                com.example.costura.R.string.detalle_medidas,
                patron.anchoCm, patron.largoCm
            )
            if (!patron.fotoUrl.isNullOrEmpty()) {
                Glide.with(this).load(patron.fotoUrl).into(binding.ivFoto)
            }
            if (!patron.tutorialUrl.isNullOrEmpty()) {
                binding.btnTutorial.visibility = View.VISIBLE
                binding.btnTutorial.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.tutorialUrl)))
                }
            }
        }

        binding.btnLike.setOnClickListener {
            viewModel.toggleLike()
        }

        binding.btnEnviar.setOnClickListener {
            val texto = binding.etComentario.text?.toString()?.trim()
            if (!texto.isNullOrEmpty()) {
                viewModel.enviarComentario(texto)
                binding.etComentario.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
