package com.example.costura.ui.subir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.costura.R
import com.example.costura.databinding.FragmentSubirBinding
import com.example.costura.ui.common.FotosAdapter
import com.example.costura.viewmodel.SubirViewModel
import com.google.android.material.snackbar.Snackbar

class SubirFragment : Fragment() {

    private var _binding: FragmentSubirBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubirViewModel by viewModels()

    private val fotosPreviewAdapter = FotosAdapter()

    private val pickFotos = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isEmpty()) return@registerForActivityResult
        viewModel.setFotosUris(uris)
    }

    private val pickPdf = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        val nombre = uri.lastPathSegment ?: "documento.pdf"
        viewModel.setPdfUri(uri, nombre)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFotosPreview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFotosPreview.adapter = fotosPreviewAdapter

        binding.btnFoto.setOnClickListener { pickFotos.launch("image/*") }
        binding.btnPdf.setOnClickListener { pickPdf.launch("application/pdf") }
        binding.btnPublicar.setOnClickListener { publicarPatron() }

        binding.btnIrExplorar.setOnClickListener {
            findNavController().navigate(R.id.explorarFragment)
        }

        binding.btnPublicarOtro.setOnClickListener {
            binding.layoutExito.visibility = View.GONE
        }

        viewModel.fotosUris.observe(viewLifecycleOwner) { uris ->
            fotosPreviewAdapter.submitList(uris.map { it.toString() })
            binding.btnFoto.text = if (uris.isEmpty())
                getString(R.string.subir_foto)
            else
                getString(R.string.subir_foto_cambiar, uris.size)
        }

        viewModel.pdfNombre.observe(viewLifecycleOwner) { nombre ->
            if (!nombre.isNullOrEmpty()) {
                binding.tvPdfNombre.visibility = View.VISIBLE
                binding.tvPdfNombre.text = nombre
            } else {
                binding.tvPdfNombre.visibility = View.GONE
            }
        }

        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            binding.btnPublicar.isEnabled = !cargando
            binding.progress.visibility = if (cargando) View.VISIBLE else View.GONE
        }

        viewModel.publicado.observe(viewLifecycleOwner) { exito ->
            if (exito) {
                limpiarFormulario()
                binding.layoutExito.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun categoriaSeleccionada(): String? {
        return when (binding.chipGroupCategoria.checkedChipId) {
            R.id.chip_ropa -> "ropa"
            R.id.chip_costura_basica -> "costura_basica"
            R.id.chip_hogar -> "hogar"
            R.id.chip_infantil -> "infantil"
            else -> null
        }
    }

    private fun dificultadSeleccionada(): String? {
        return when (binding.chipGroupDificultad.checkedChipId) {
            R.id.chip_facil -> "fácil"
            R.id.chip_medio -> "medio"
            R.id.chip_dificil -> "difícil"
            else -> null
        }
    }

    private fun publicarPatron() {
        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val descripcion = binding.etDescripcion.text?.toString()?.trim() ?: ""
        val anchoStr = binding.etAncho.text?.toString()?.trim() ?: ""
        val largoStr = binding.etLargo.text?.toString()?.trim() ?: ""
        val categoria = categoriaSeleccionada()
        val dificultad = dificultadSeleccionada()
        val fotos = viewModel.fotosUris.value ?: emptyList()

        if (fotos.isEmpty()) {
            Snackbar.make(binding.root, R.string.subir_foto_requerida, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (nombre.isEmpty() || descripcion.isEmpty() || anchoStr.isEmpty() || largoStr.isEmpty()) {
            Snackbar.make(binding.root, R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (categoria == null) {
            Snackbar.make(binding.root, R.string.subir_categoria_requerida, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (dificultad == null) {
            Snackbar.make(binding.root, R.string.subir_dificultad_requerida, Snackbar.LENGTH_SHORT).show()
            return
        }

        val ancho = anchoStr.toFloatOrNull() ?: return
        val largo = largoStr.toFloatOrNull() ?: return
        val consejos = binding.etConsejos.text?.toString()?.trim() ?: ""
        val tutorial = binding.etTutorial.text?.toString()?.trim()

        viewModel.publicar(nombre, descripcion, categoria, dificultad, ancho, largo, consejos, tutorial)
    }

    private fun limpiarFormulario() {
        binding.etNombre.text?.clear()
        binding.etDescripcion.text?.clear()
        binding.etAncho.text?.clear()
        binding.etLargo.text?.clear()
        binding.etConsejos.text?.clear()
        binding.etTutorial.text?.clear()
        binding.chipGroupCategoria.clearCheck()
        binding.chipGroupDificultad.clearCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
