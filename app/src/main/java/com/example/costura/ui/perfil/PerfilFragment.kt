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
import com.example.costura.ui.explorar.PatronComunidadAdapter
import com.example.costura.ui.login.LoginActivity
import com.example.costura.viewmodel.PerfilViewModel

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerfilViewModel by viewModels()

    private val misPatronesAdapter = PatronComunidadAdapter { patron ->
        findNavController().navigate(R.id.detalleFragment, bundleOf("patronId" to patron.id))
    }

    private val guardadosAdapter = PatronComunidadAdapter { patron ->
        findNavController().navigate(R.id.detalleFragment, bundleOf("patronId" to patron.id))
    }

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

        binding.btnHistorial.setOnClickListener {
            findNavController().navigate(R.id.action_perfil_to_historial)
        }

        binding.btnCerrarSesion.setOnClickListener {
            viewModel.cerrarSesion()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario == null) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
                return@observe
            }
            binding.tvNombre.text = usuario.nombreUsuario
            binding.tvEmail.text = usuario.email
            if (!usuario.fotoUrl.isNullOrEmpty()) {
                Glide.with(this).load(usuario.fotoUrl).circleCrop().into(binding.ivAvatar)
            }
        }

        viewModel.misPatrones.observe(viewLifecycleOwner) { patrones ->
            misPatronesAdapter.submitList(patrones)
            binding.tvMisPatrones.visibility = if (patrones.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.guardados.observe(viewLifecycleOwner) { patrones ->
            guardadosAdapter.submitList(patrones)
            binding.tvGuardados.visibility = if (patrones.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
