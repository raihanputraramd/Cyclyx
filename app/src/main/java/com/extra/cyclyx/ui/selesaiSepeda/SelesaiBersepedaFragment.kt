package com.extra.cyclyx.ui.selesaiSepeda


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.extra.cyclyx.R
import com.extra.cyclyx.databinding.FragmentSelesaiSepedaBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd


/**
 * A simple [Fragment] subclass.
 */
class SelesaiBersepedaFragment : Fragment() {
    lateinit var binding : FragmentSelesaiSepedaBinding
    lateinit var viewModel : SelesaiBersepedaViewModel
    lateinit var interstitialAd : InterstitialAd


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelesaiSepedaBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val app = requireActivity().application
        val arguments = SelesaiBersepedaFragmentArgs.fromBundle(arguments!!)
        viewModel = ViewModelProvider(this,SelesaiBersepedaViewModel.Factory(arguments.bersepedaKey,app)).get(SelesaiBersepedaViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.roundback.visibility = View.VISIBLE
        binding.roundback.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open))

        viewModel.act.observe(viewLifecycleOwner, Observer {act ->
            act?.let{
                viewModel.onActLoaded(it)
            }
        })

        viewModel.goToHasil.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.navigateToResult(it.id)
            }
        })

        viewModel.goToHome.observe(viewLifecycleOwner, Observer {
            it?.let {
//                val intent = Intent(requireActivity(),MainActivity::class.java)
//                startActivity(intent)
                requireActivity().finish()
            }
        })
    }

    private fun navigateToResult(id : Long){
        this.findNavController().navigate(SelesaiBersepedaFragmentDirections.navigateToHasilBersepedaFromSelesaiBersepeda(id))
    }


}
