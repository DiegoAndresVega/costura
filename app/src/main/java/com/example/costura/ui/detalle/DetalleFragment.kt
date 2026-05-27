package com.example.costura.ui.detalle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.costura.R
import com.example.costura.databinding.FragmentDetalleBinding
import com.example.costura.ui.common.FotosAdapter
import com.example.costura.viewmodel.DetalleViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val patronId by lazy { arguments?.getString("patronId") ?: "" }

    private val viewModel: DetalleViewModel by viewModels {
        DetalleViewModel.Factory(patronId)
    }

    private val fotosAdapter = FotosAdapter()
    private val currentUid by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    private val comentariosAdapter by lazy {
        ComentarioAdapter(
            currentUid = currentUid,
            onDelete = { id -> viewModel.eliminarComentario(id) }
        )
    }

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
                binding.chipDificultad.text = patron.dificultad.replaceFirstChar { it.uppercase() }
                val (bgColor, textColor) = when (patron.dificultad.lowercase()) {
                    "fácil" -> Pair(R.color.dificultad_facil, R.color.dificultad_facil_text)
                    "medio" -> Pair(R.color.dificultad_medio, R.color.dificultad_medio_text)
                    else -> Pair(R.color.dificultad_dificil, R.color.dificultad_dificil_text)
                }
                binding.chipDificultad.setChipBackgroundColorResource(bgColor)
                binding.chipDificultad.setTextColor(ContextCompat.getColor(requireContext(), textColor))
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

            binding.tvNombreAutor.text = patron.nombreAutor
            if (!patron.fotoAutorUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(patron.fotoAutorUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.imgAvatarAutor)
            } else {
                binding.imgAvatarAutor.setImageResource(R.drawable.ic_person)
            }

            if (!patron.tutorialUrl.isNullOrEmpty()) {
                binding.layoutBotonesRecursos.visibility = View.VISIBLE
                binding.btnTutorial.visibility = View.VISIBLE
                binding.btnTutorial.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.tutorialUrl)))
                }
            }

            if (!patron.pdfUrl.isNullOrEmpty()) {
                binding.layoutBotonesRecursos.visibility = View.VISIBLE
                binding.btnPdf.visibility = View.VISIBLE
                binding.btnPdf.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(patron.pdfUrl)))
                }
            }
        }

        viewModel.esAutor.observe(viewLifecycleOwner) { esAutor ->
            val visibilidad = if (esAutor) View.VISIBLE else View.GONE
            binding.btnEditar.visibility = visibilidad
            binding.btnEliminar.visibility = visibilidad
        }

        viewModel.eliminado.observe(viewLifecycleOwner) { eliminado ->
            if (eliminado) findNavController().navigateUp()
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.comentarios.observe(viewLifecycleOwner) { lista ->
            comentariosAdapter.submitList(lista)
        }

        viewModel.liked.observe(viewLifecycleOwner) { liked ->
            binding.btnLike.setIconResource(
                if (liked) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )
        }

        viewModel.guardado.observe(viewLifecycleOwner) { guardado ->
            binding.btnGuardar.setIconResource(
                if (guardado) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
            )
        }

        binding.btnLike.setOnClickListener { viewModel.toggleLike() }
        binding.btnGuardar.setOnClickListener { viewModel.toggleGuardado() }

        binding.btnEditar.setOnClickListener {
            findNavController().navigate(
                R.id.editarPatronFragment,
                bundleOf("patronId" to patronId)
            )
        }

        binding.btnEliminar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.eliminar_titulo)
                .setMessage(R.string.eliminar_mensaje)
                .setNegativeButton(R.string.eliminar_cancelar, null)
                .setPositiveButton(R.string.eliminar_confirmar) { _, _ ->
                    viewModel.eliminar()
                }
                .show()
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
