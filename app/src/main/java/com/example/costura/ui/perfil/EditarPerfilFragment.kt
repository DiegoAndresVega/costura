package com.example.costura.ui.perfil

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.costura.R
import com.example.costura.databinding.FragmentEditarPerfilBinding
import com.example.costura.model.Usuario
import com.example.costura.viewmodel.EditarPerfilViewModel
import com.google.android.material.snackbar.Snackbar

class EditarPerfilFragment : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditarPerfilViewModel by viewModels()
    private var fotoUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        fotoUri = uri
        Glide.with(this).load(uri).circleCrop()
            .placeholder(R.drawable.ic_person)
            .into(binding.ivFoto)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivFoto.setOnClickListener { pickImage.launch("image/*") }
        binding.btnCambiarFoto.setOnClickListener { pickImage.launch("image/*") }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text?.toString() ?: ""
            val bio = binding.etBio.text?.toString() ?: ""
            val nivel = when (binding.chipGroupNivel.checkedChipId) {
                R.id.chip_principiante -> "principiante"
                R.id.chip_intermedio -> "intermedio"
                R.id.chip_avanzado -> "avanzado"
                else -> ""
            }
            viewModel.guardar(nombre, bio, nivel, fotoUri)
        }

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario != null) mostrarDatos(usuario)
        }

        viewModel.guardando.observe(viewLifecycleOwner) { guardando ->
            binding.progressBar.visibility = if (guardando) View.VISIBLE else View.GONE
            binding.btnGuardar.isEnabled = !guardando
        }

        viewModel.exito.observe(viewLifecycleOwner) { exito ->
            if (exito) findNavController().popBackStack()
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, R.string.perfil_guardado_error, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDatos(u: Usuario) {
        binding.etNombre.setText(u.nombreMostrado.ifBlank { u.nombreUsuario })
        binding.etBio.setText(u.bio)

        when (u.nivelCostura) {
            "principiante" -> binding.chipPrincipiante.isChecked = true
            "intermedio" -> binding.chipIntermedio.isChecked = true
            "avanzado" -> binding.chipAvanzado.isChecked = true
        }

        val foto = u.fotoEfectiva
        if (!foto.isNullOrEmpty()) {
            Glide.with(this).load(foto).circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.ivFoto)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
