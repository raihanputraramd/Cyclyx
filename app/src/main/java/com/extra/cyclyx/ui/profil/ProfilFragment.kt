package com.extra.cyclyx.ui.profil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.extra.cyclyx.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var viewModel: ProfilViewModel
    private lateinit var btnSettings: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(this, ProfilViewModel.Factory(application)).get(
            ProfilViewModel::class.java)
        binding.viewModel = viewModel

        binding.BtnEditProfil.setOnClickListener{
            this.findNavController().navigate(ProfilFragmentDirections.navigateToEditProfile())
        }
        return binding.root
    }

}