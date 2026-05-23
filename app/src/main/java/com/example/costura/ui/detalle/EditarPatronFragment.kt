package com.example.costura.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.costura.R
import com.example.costura.databinding.FragmentEditarPatronBinding
import com.example.costura.viewmodel.EditarPatronViewModel
import com.google.android.material.snackbar.Snackbar

class EditarPatronFragment : Fragment() {

    private var _binding: FragmentEditarPatronBinding? = null
    private val binding get() = _binding!!

    private val patronId by lazy { arguments?.getString("patronId") ?: "" }

    private val viewModel: EditarPatronViewModel by viewModels {
        EditarPatronViewModel.Factory(patronId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPatronBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.patron.observe(viewLifecycleOwner) { patron ->
            patron ?: return@observe
            binding.etNombre.setText(patron.nombre)
            binding.etDescripcion.setText(patron.descripcion)
            binding.etAncho.setText(if (patron.anchoCm > 0) patron.anchoCm.toInt().toString() else "")
            binding.etLargo.setText(if (patron.largoCm > 0) patron.largoCm.toInt().toString() else "")
            binding.etConsejos.setText(patron.consejos)
            binding.etTutorial.setText(patron.tutorialUrl ?: "")

            val categoriaChip = when (patron.categoria) {
                "ropa" -> R.id.chip_ropa
                "costura_basica" -> R.id.chip_costura_basica
                "hogar" -> R.id.chip_hogar
                "infantil" -> R.id.chip_infantil
                else -> View.NO_ID
            }
            if (categoriaChip != View.NO_ID) binding.chipGroupCategoria.check(categoriaChip)

            val dificultadChip = when (patron.dificultad.lowercase()) {
                "fácil" -> R.id.chip_facil
                "medio" -> R.id.chip_medio
                "difícil" -> R.id.chip_dificil
                else -> View.NO_ID
            }
            if (dificultadChip != View.NO_ID) binding.chipGroupDificultad.check(dificultadChip)
        }

        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            binding.btnGuardar.isEnabled = !cargando
            binding.progress.visibility = if (cargando) View.VISIBLE else View.GONE
        }

        viewModel.guardado.observe(viewLifecycleOwner) { guardado ->
            if (guardado) {
                Snackbar.make(binding.root, R.string.editar_guardado_ok, Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnGuardar.setOnClickListener { guardarCambios() }
    }

    private fun categoriaSeleccionada(): String? = when (binding.chipGroupCategoria.checkedChipId) {
        R.id.chip_ropa -> "ropa"
        R.id.chip_costura_basica -> "costura_basica"
        R.id.chip_hogar -> "hogar"
        R.id.chip_infantil -> "infantil"
        else -> null
    }

    private fun dificultadSeleccionada(): String? = when (binding.chipGroupDificultad.checkedChipId) {
        R.id.chip_facil -> "fácil"
        R.id.chip_medio -> "medio"
        R.id.chip_dificil -> "difícil"
        else -> null
    }

    private fun guardarCambios() {
        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val descripcion = binding.etDescripcion.text?.toString()?.trim() ?: ""
        val anchoStr = binding.etAncho.text?.toString()?.trim() ?: ""
        val largoStr = binding.etLargo.text?.toString()?.trim() ?: ""
        val categoria = categoriaSeleccionada()
        val dificultad = dificultadSeleccionada()

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

        viewModel.actualizar(nombre, descripcion, categoria, dificultad, ancho, largo, consejos, tutorial)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
