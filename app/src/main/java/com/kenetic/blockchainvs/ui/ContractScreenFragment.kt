package com.kenetic.blockchainvs.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.PartyEnum
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.VoteContractDelegate
import com.kenetic.blockchainvs.databinding.FragmentContractScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ContractScreenFragment"

class ContractScreenFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as ApplicationStarter).database.partyDao())
    }

    private lateinit var binding: FragmentContractScreenBinding
    private var partyEnum = PartyEnum.ONE
    private var voteContractDelegate = VoteContractDelegate()

    private val transactionInProgress = "Transaction currently in progress..."
    private val calling = "calling function..."

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContractScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyBinding() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController()
                    .navigate(
                        ContractScreenFragmentDirections
                            .actionContractScreenFragmentToMainScreenFragment()
                    )
            }
            //--------------------------------------------------------------------------data-binding
            dataBindingViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

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
            //----------------------------------------------------------------------------caste-vote
            casteVoteButton.setOnClickListener {
                viewModel.transactionCost.value = transactionInProgress
                CoroutineScope(Dispatchers.IO).launch {
                    val cost = voteContractDelegate.casteVote(partyEnum)
                    Log.d(TAG, "transaction output:-\n$cost")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.transactionCost.value = cost
                    }
                }
            }
            //----------------------------------------------------------------------------get-voters
            getRegisteredVotersButton.setOnClickListener {
                viewModel.addressList.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val votersListAsString = voteContractDelegate.getVoterAddresses()
                    Log.d(TAG, "registered voter addresses = $votersListAsString")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.addressList.value = votersListAsString
                    }
                }
            }
            //------------------------------------------------------------------------has-user-voted
            checkVoterStatusButton.setOnClickListener {
                viewModel.alreadyVoted.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val hasUserVoted = voteContractDelegate.getHasAlreadyVoted()
                    Log.d(TAG, "voting status = $hasUserVoted")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.alreadyVoted.value = hasUserVoted
                    }
                }
            }
            //------------------------------------------------------------------get-voters-addresses
            getVotesButton.setOnClickListener {
                viewModel.allPartyVotes.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val electionStatus = voteContractDelegate.partyVotesStatus()
                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.allPartyVotes.value = electionStatus
                    }
                }
            }

            getBalanceButton.setOnClickListener {
                viewModel.balance.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val balanceReceived = voteContractDelegate.getBalance()
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.balance.value = "$balanceReceived ETH"
                    }
                }
            }

            testButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    voteContractDelegate.testingFunction()
                }
            }
        }
    }
}