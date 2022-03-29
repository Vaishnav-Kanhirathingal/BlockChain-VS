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
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
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
    private val transactionInProgress = "Transaction currently in progress..."
    private val calling = "calling function..."
    private val unknown = "Unknown..."

    //------------------------------------------------------------------------data-binding-live-data
    private val callNotPerformedYet = "Call Not Performed Yet"
    val transactionCost: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val addressList: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val alreadyVoted: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val allPartyVotes: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val balance: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)

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
                    .navigate(ContractScreenFragmentDirections.actionContractScreenFragmentToMainScreenFragment())
            }
            //--------------------------------------------------------------------------data-binding
            contractFragment = this@ContractScreenFragment
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
            // TODO: solve errors
            casteVoteButton.setOnClickListener {
                transactionCost.value = transactionInProgress
                CoroutineScope(Dispatchers.IO).launch {
                    val cost = voteContractDelegate.casteVote(partyEnum)
                    Log.d(TAG, "transaction output:-\n$cost")
                    CoroutineScope(Dispatchers.Main).launch {
                        transactionCost.value = cost
                    }
                }
            }
            //----------------------------------------------------------------------------get-voters
            getRegisteredVotersButton.setOnClickListener {
                addressList.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val votersListAsString = voteContractDelegate.getVoterAddresses()
                    Log.d(TAG, "registered voter addresses = $votersListAsString")
                    CoroutineScope(Dispatchers.Main).launch {
                        addressList.value = votersListAsString
                    }
                }
            }
            //------------------------------------------------------------------------has-user-voted
            checkVoterStatusButton.setOnClickListener {
                alreadyVoted.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val hasUserVoted = voteContractDelegate.getHasAlreadyVoted()
                    Log.d(TAG, "voting status = $hasUserVoted")
                    CoroutineScope(Dispatchers.Main).launch {
                        alreadyVoted.value = hasUserVoted
                    }
                }
            }
            //------------------------------------------------------------------get-voters-addresses
            getVotesButton.setOnClickListener {
                allPartyVotes.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val electionStatus = voteContractDelegate.partyVotesStatus()
                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        allPartyVotes.value = electionStatus
                    }
                }
            }
            getBalanceButton.setOnClickListener {
                balance.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val balanceReceived = voteContractDelegate.getBalance()
                    CoroutineScope(Dispatchers.Main).launch {
                        balance.value = "$balanceReceived ETH"
                    }
                }
            }
            testingButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    voteContractDelegate.testingFunction()
                }
            }
        }
    }
}