package com.example.costura.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.costura.R
import com.example.costura.databinding.FragmentPerfilBinding
import com.example.costura.model.Usuario
import com.example.costura.ui.explorar.PatronComunidadAdapter
import com.example.costura.ui.login.LoginActivity
import com.example.costura.viewmodel.PerfilViewModel
import com.google.android.material.snackbar.Snackbar

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()

    private val misPatronesAdapter = PatronComunidadAdapter(
        onClick = { patron ->
            findNavController().navigate(R.id.action_perfil_to_detalle, bundleOf("patronId" to patron.id))
        }
    )

    private val guardadosAdapter = PatronComunidadAdapter(
        onClick = { patron ->
            findNavController().navigate(R.id.action_perfil_to_detalle, bundleOf("patronId" to patron.id))
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMisPatrones.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisPatrones.adapter = misPatronesAdapter

        binding.rvGuardados.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGuardados.adapter = guardadosAdapter

        binding.btnEditarPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_perfil_to_editar)
        }

        binding.btnHistorial.setOnClickListener {
            findNavController().navigate(R.id.action_perfil_to_historial)
        }

        binding.btnCerrarSesion.setOnClickListener {
            viewModel.cerrarSesion()
        }

        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario == null) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
                return@observe
            }
            mostrarUsuario(usuario)
        }

        viewModel.misPatrones.observe(viewLifecycleOwner) { patrones ->
            misPatronesAdapter.submitList(patrones)
            binding.tvMisPatrones.visibility = if (patrones.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.guardados.observe(viewLifecycleOwner) { patrones ->
            guardadosAdapter.submitList(patrones)
            binding.tvGuardados.visibility = if (patrones.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.recargar()
    }

    private fun mostrarUsuario(usuario: Usuario) {
        binding.tvNombre.text = usuario.nombreEfectivo
        binding.tvEmail.text = usuario.email

        val foto = usuario.fotoEfectiva
        if (!foto.isNullOrEmpty()) {
            Glide.with(this).load(foto).circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.ivAvatar)
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_person)
        }

        if (usuario.bio.isNotBlank()) {
            binding.tvBio.text = usuario.bio
            binding.tvBio.visibility = View.VISIBLE
        } else {
            binding.tvBio.visibility = View.GONE
        }

        if (usuario.nivelCostura.isNotBlank()) {
            val nivelTexto = when (usuario.nivelCostura) {
                "principiante" -> getString(R.string.perfil_nivel_principiante)
                "intermedio" -> getString(R.string.perfil_nivel_intermedio)
                "avanzado" -> getString(R.string.perfil_nivel_avanzado)
                else -> usuario.nivelCostura
            }
            binding.chipNivel.text = nivelTexto
            binding.chipNivel.visibility = View.VISIBLE
        } else {
            binding.chipNivel.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
