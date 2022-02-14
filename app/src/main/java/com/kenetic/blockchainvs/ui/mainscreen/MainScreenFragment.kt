package com.kenetic.blockchainvs.ui.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding

class MainScreenFragment : Fragment() {
    private lateinit var binding :FragmentMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()

    }

    private fun applyBinding(){
        binding.apply {
            // TODO: apply binding to the given views
        }
    }
}