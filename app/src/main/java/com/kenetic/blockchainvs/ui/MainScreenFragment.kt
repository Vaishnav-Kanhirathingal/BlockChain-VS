package com.kenetic.blockchainvs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding
import com.kenetic.blockchainvs.recycler.PartyListAdapter

class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var partyAdapter: PartyListAdapter
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as ApplicationStarter).database.partyDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partyAdapter = PartyListAdapter(viewModel)
        viewModel.getAllById().asLiveData().observe(viewLifecycleOwner) {
            partyAdapter.submitList(it)
        }
        applyBinding()
    }

    private fun applyBinding() {
        binding.apply {
            includedSubLayout.partyRecyclerView.adapter = partyAdapter
        }
    }
}