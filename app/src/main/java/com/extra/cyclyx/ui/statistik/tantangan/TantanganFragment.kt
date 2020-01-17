package com.extra.cyclyx.ui.statistik.tantangan


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.extra.cyclyx.databinding.FragmentTantanganBinding
import com.extra.cyclyx.utils.adapter.TantanganAdapter
import com.extra.cyclyx.utils.adapter.TantanganClickListener

/**
 * A simple [Fragment] subclass.
 */
class TantanganFragment : Fragment() {
    private lateinit var binding: FragmentTantanganBinding
    private lateinit var viewModel: TantanganViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTantanganBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProviders.of(this, TantanganViewModel.Factory(application)).get(
            TantanganViewModel::class.java)

        binding.viewModel = viewModel

        binding.rvPencapaian.layoutManager = LinearLayoutManager(context)
        val adapter = TantanganAdapter(TantanganClickListener{ actId, action -> id })
        binding.rvPencapaian.adapter = adapter


        viewModel.challenges.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
