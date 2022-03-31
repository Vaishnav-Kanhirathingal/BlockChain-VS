package com.kenetic.blockchainvs.block_connector.contract.reference_code

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
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.PartyEnum
import com.kenetic.blockchainvs.databinding.FragmentContractScreenBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.TestEnum
import com.kenetic.blockchainvs.ui.ContractScreenFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

private const val TAG = "ContractScreensFragment"

class ContractScreensFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as ApplicationStarter).database.partyDao())
    }

    private lateinit var binding: FragmentContractScreenBinding
    private var partyEnum = PartyEnum.ONE

    private val transactionInProgress = "Transaction currently in progress..."
    private val calling = "calling function..."

    private val accountDataStore = AccountDataStore(requireContext())

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
            // TODO: solve errors
            casteVoteButton.setOnClickListener {
                viewModel.transactionCost.value = transactionInProgress
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((30000..60000).random().toLong())
                    val rand = ((1000000..3000000).random())
                    val cost = BigDecimal("0.00016$rand")
                    Log.d(TAG, "transaction output:-\n$cost")

                    val currentBalance =
                        BigDecimal(accountDataStore.balanceFlow.asLiveData().value.toString())
                    Log.d(TAG, "pre transaction balance -\t$currentBalance")

                    val deducedBalance = currentBalance - cost
                    Log.d(TAG, "post transaction balance -\t$deducedBalance")
                    accountDataStore.testSetter(
                        requireContext(),
                        TestEnum.BALANCE,
                        0,
                        deducedBalance.toString(),
                        false
                    )
                    accountDataStore.testSetter(
                        requireContext(),
                        when (partyEnum) {
                            PartyEnum.ONE -> TestEnum.V1
                            PartyEnum.TWO -> TestEnum.V2
                            PartyEnum.THREE -> TestEnum.V3
                        },
                        1,
                        deducedBalance.toString(),
                        false
                    )
                    accountDataStore.testSetter(
                        requireContext(), TestEnum.HV,
                        1,
                        deducedBalance.toString(),
                        true
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.transactionCost.value = cost.toString()
                    }
                }
            }

            //------------------------------------------------------------------------has-user-voted
            checkVoterStatusButton.setOnClickListener {
                viewModel.alreadyVoted.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val hasUserVoted = accountDataStore.hvFlow.asLiveData().toString()
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
                    val electionStatus =
                        "party 1 votes = " +
                                accountDataStore.v1Flow.asLiveData().toString() +
                                "\nparty 2 votes = " +
                                accountDataStore.v2Flow.asLiveData().toString() +
                                "\nparty 3 votes = " +
                                accountDataStore.v3Flow.asLiveData().toString()

                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.allPartyVotes.value = electionStatus
                    }
                }
            }
            getBalanceButton.setOnClickListener {
                viewModel.balance.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    val balanceReceived = accountDataStore.balanceFlow.asLiveData().toString()
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.balance.value = "$balanceReceived ETH"
                    }
                }
            }
        }
    }
}