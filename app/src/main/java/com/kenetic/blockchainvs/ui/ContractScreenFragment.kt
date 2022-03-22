package com.kenetic.blockchainvs.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.PartyEnum
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.VoteContractDelegate
import com.kenetic.blockchainvs.databinding.FragmentContractScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ContractScreenFragment"

class ContractScreenFragment : Fragment() {
    private lateinit var binding: FragmentContractScreenBinding
    private var partyEnum = PartyEnum.ONE
    private var voteContractDelegate = VoteContractDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContractScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
    }

    fun applyBinding() {
        //--------------------------------------------------------------------------------caste-vote
        binding.apply {
            casteVoteButton.isEnabled = false
            partySelectorRadioGroup.setOnCheckedChangeListener(
                fun(r: RadioGroup, i: Int) {
                    casteVoteButton.isEnabled = true
                    partyEnum = when (i) {
                        R.id.party_one_radio_button -> {
                            PartyEnum.ONE
                        }
                        R.id.party_two_radio_button -> {
                            PartyEnum.TWO
                        }
                        else -> {
                            PartyEnum.THREE
                        }
                    }
                }
            )
            casteVoteButton.setOnClickListener {
                var cost: MutableLiveData<Int> = MutableLiveData<Int>(0)
                    cost.value = voteContractDelegate.casteVote(partyEnum)
                cost.observe(viewLifecycleOwner) {
                    Toast.makeText(
                        requireContext(),
                        "cost of transfer = ${cost}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // TODO: display cost on ui
            }
            getRegisteredVotersButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val votersListAsString = voteContractDelegate.getVoterAddresses()
                    Log.d(TAG, "addresses returned -\n$votersListAsString")
                    // TODO: display on ui}
                }
            }
            checkVoterStatusButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasUserVoted = voteContractDelegate.getHasAlreadyVoted()
                    Log.d(
                        TAG, "voting status = $hasUserVoted"
                    )
                    // TODO: display on ui}
                }
            }
            getVotesButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    // TODO: call respective contract function
                    val electionStatus = voteContractDelegate.partyVotesStatus()
                    Log.d(TAG, "election status received: \n$electionStatus")
                    // TODO: display on ui}
                }
            }
            testingButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    voteContractDelegate.abc()
                }
            }
        }
    }
}