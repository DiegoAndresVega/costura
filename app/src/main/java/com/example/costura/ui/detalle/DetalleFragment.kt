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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.costura.R
import com.example.costura.databinding.FragmentDetalleBinding
import com.example.costura.ui.common.FotosAdapter
import com.example.costura.viewmodel.DetalleViewModel

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val patronId by lazy { arguments?.getString("patronId") ?: "" }

    private val viewModel: DetalleViewModel by viewModels {
        DetalleViewModel.Factory(patronId)
    }

    private val fotosAdapter = FotosAdapter()
    private val comentariosAdapter = ComentarioAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFotos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFotos.adapter = fotosAdapter

        binding.rvComentarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComentarios.adapter = comentariosAdapter

        viewModel.patron.observe(viewLifecycleOwner) { patron ->
            patron ?: return@observe

            fotosAdapter.submitList(patron.fotosUrls)

            binding.tvNombre.text = patron.nombre
            if (patron.categoria.isNotBlank()) {
                binding.chipCategoria.visibility = View.VISIBLE
                binding.chipCategoria.text = patron.categoria
                    .replace("_", " ")
                    .replaceFirstChar { it.uppercase() }
            } else {
                binding.chipCategoria.visibility = View.GONE
            }

            if (patron.dificultad.isNotBlank()) {
                binding.chipDificultad.visibility = View.VISIBLE
                binding.chipDificultad.text = patron.dificultad
                    .replaceFirstChar { it.uppercase() }
            } else {
                binding.chipDificultad.visibility = View.GONE
            }
            binding.tvMedidas.text = getString(
                R.string.detalle_medidas, patron.anchoCm, patron.largoCm
            )
            binding.tvDescripcion.text = patron.descripcion

            if (patron.consejos.isNotBlank()) {
                binding.tvConsejosTitulo.visibility = View.VISIBLE
                binding.tvConsejos.visibility = View.VISIBLE
                binding.tvConsejos.text = patron.consejos
            }

            if (!patron.tutorialUrl.isNullOrEmpty()) {
                binding.btnTutorial.visibility = View.VISIBLE
                binding.btnTutorial.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.tutorialUrl)))
                }
            }

            if (!patron.pdfUrl.isNullOrEmpty()) {
                binding.btnPdf.visibility = View.VISIBLE
                binding.btnPdf.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.pdfUrl)))
                }
            }
        }

        viewModel.comentarios.observe(viewLifecycleOwner) { lista ->
            comentariosAdapter.submitList(lista)
        }

        viewModel.liked.observe(viewLifecycleOwner) { liked ->
            binding.btnLike.setIconResource(
                if (liked) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }

        binding.btnLike.setOnClickListener { viewModel.toggleLike() }

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
