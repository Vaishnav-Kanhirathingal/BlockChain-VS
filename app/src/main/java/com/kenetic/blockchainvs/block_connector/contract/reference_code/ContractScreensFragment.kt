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
import com.kenetic.blockchainvs.block_connector.contract.contract_interface.VoteContractDelegate
import com.kenetic.blockchainvs.databinding.FragmentContractScreensBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.TestEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigDecimal

private const val TAG = "ContractScreensFragment"

class ContractScreensFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as ApplicationStarter).database.partyDao())
    }

    private lateinit var binding: FragmentContractScreensBinding
    private var partyEnum = PartyEnum.ONE
    private lateinit var accountDataStore: AccountDataStore
    private val transactionInProgress = "Transaction currently in progress..."
    private val calling = "calling function..."
    private val ACCOUNT_ADDRESS = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"

    private var voteForPartyOne: Int = 0
    private var voteForPartyTwo: Int = 0
    private var voteForPartyThree: Int = 0
    private var balance: String = "0"
    private var accList: String = "0"
    private var hasUserVoted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContractScreensBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountDataStore = AccountDataStore(requireContext())

        accountDataStore.v1Flow.asLiveData().observe(viewLifecycleOwner) {
            voteForPartyOne = it
        }
        accountDataStore.v2Flow.asLiveData().observe(viewLifecycleOwner) {
            voteForPartyTwo = it
        }
        accountDataStore.v3Flow.asLiveData().observe(viewLifecycleOwner) {
            voteForPartyThree = it
        }
        accountDataStore.balanceFlow.asLiveData().observe(viewLifecycleOwner) {
            balance = it
        }
        accountDataStore.adrFlow.asLiveData().observe(viewLifecycleOwner) {
            accList = it
        }
        accountDataStore.hvFlow.asLiveData().observe(viewLifecycleOwner) {
            hasUserVoted = it
        }

        Log.d(
            TAG,
            "voteForPartyOne = ${voteForPartyOne}" +
                    "\nvoteForPartyTwo = ${voteForPartyTwo}" +
                    "\nvoteForPartyThree = ${voteForPartyThree}" +
                    "\nbalance = ${balance}" +
                    "\naccList = ${accList}" +
                    "\nhasUserVoted = ${hasUserVoted}"
        )
        applyBinding()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyBinding() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController()
                    .navigate(
                        ContractScreensFragmentDirections
                            .actionContractScreensFragmentToMainScreenFragment()
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
            // TODO: solve errors
            casteVoteButton.setOnClickListener {
                viewModel.transactionCost.value = transactionInProgress
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((15000..60000).random().toLong())

                    val rand = ((10000000..90000000).random())
                    val cost = BigDecimal("0.00016$rand")
                    Log.d(TAG, "transaction output cost:-\n$cost")

                    val currentBalance =
                        BigDecimal(balance.toString())
                    Log.d(TAG, "pre transaction balance -\t$currentBalance")

                    val deducedBalance = currentBalance - cost
                    Log.d(TAG, "post transaction balance -\t$deducedBalance")
                    //-----------------------------------------------------------------store-balance

                    addBalance(deducedBalance.toString())
                    addPartyVotes(partyEnum)

                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.transactionCost.value = cost.toString()
                    }
                }
            }

            //-------------------------------------------------------------------------get-addresses
            getRegisteredVotersButton.setOnClickListener {
                // TODO: store to this
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.addressList.value = accList
                }
            }

            //------------------------------------------------------------------------has-user-voted
            checkVoterStatusButton.setOnClickListener {
                viewModel.alreadyVoted.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((500..2000).random().toLong())
                    Log.d(TAG, "voting status = ${hasUserVoted}")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.alreadyVoted.value = hasUserVoted.toString()
                    }
                }
            }
            //------------------------------------------------------------------get-voters-addresses
            getVotesButton.setOnClickListener {
                viewModel.allPartyVotes.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((500..2000).random().toLong())
                    val electionStatus =
                        "party 1 votes = " + voteForPartyOne +
                                "\nparty 2 votes = " + voteForPartyTwo +
                                "\nparty 3 votes = " + voteForPartyThree

                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.allPartyVotes.value = electionStatus
                    }
                }
            }

            getBalanceButton.setOnClickListener {
                viewModel.balance.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((500..2000).random().toLong())
                    val balanceReceived = balance
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.balance.value = "$balanceReceived ETH"
                    }
                }
            }
        }
    }

    private fun addBalance(currentBalance: String) {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.testSetter(
                context = requireContext(),
                testEnum = TestEnum.BALANCE,
                v1v2v3 = 0,
                bal = currentBalance,
                hv = false,
                acc = ""
            )
        }
    }

    private fun addPartyVotes(partyEnum: PartyEnum) {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.testSetter(
                context = requireContext(),
                testEnum = when (partyEnum) {
                    PartyEnum.ONE -> TestEnum.V1
                    PartyEnum.TWO -> TestEnum.V2
                    PartyEnum.THREE -> TestEnum.V3
                },
                v1v2v3 = 1 + when (partyEnum) {
                    PartyEnum.ONE -> voteForPartyOne
                    PartyEnum.TWO -> voteForPartyTwo
                    PartyEnum.THREE -> voteForPartyThree
                },
                bal = "",
                hv = false,
                acc = ""
            )
        }
    }

    private fun userHasVoted() {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.testSetter(
                context = requireContext(),
                testEnum = TestEnum.HV,
                v1v2v3 = 0,
                bal = "",
                hv = true,
                acc = ""
            )
        }
    }

    private fun addVoterToDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.testSetter(
                context = requireContext(),
                testEnum = TestEnum.ADR,
                v1v2v3 = 0,
                bal = "",
                hv = false,
                acc = ACCOUNT_ADDRESS
            )
        }
    }
}