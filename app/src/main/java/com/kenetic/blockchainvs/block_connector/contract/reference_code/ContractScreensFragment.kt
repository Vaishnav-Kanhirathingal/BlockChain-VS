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
import androidx.lifecycle.LiveData
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
    private val ACCOUT_ADDRESS = Credentials.create(VoteContractDelegate().USER_PRIVATE_KEY).address

    private lateinit var voteForPartyOne: LiveData<Int>
    private lateinit var voteForPartyTwo: LiveData<Int>
    private lateinit var voteForPartyThree: LiveData<Int>
    private lateinit var balance: LiveData<String>
    private lateinit var accList: LiveData<String>
    private lateinit var hasUserVoted: LiveData<Boolean>

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

        voteForPartyOne = accountDataStore.v1Flow.asLiveData()
        voteForPartyTwo = accountDataStore.v2Flow.asLiveData()
        voteForPartyThree = accountDataStore.v3Flow.asLiveData()
        balance = accountDataStore.balanceFlow.asLiveData()
        accList = accountDataStore.adrFlow.asLiveData()
        hasUserVoted = accountDataStore.hvFlow.asLiveData()

        Log.d(
            TAG,
            "voteForPartyOne = ${voteForPartyOne.value}" +
                    "\nvoteForPartyTwo = ${voteForPartyTwo.value}" +
                    "\nvoteForPartyThree = ${voteForPartyThree.value}" +
                    "\nbalance = ${balance.value}" +
                    "\naccList = ${accList.value}" +
                    "\nhasUserVoted = ${hasUserVoted.value}"
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
                    Thread.sleep((3000..6000).random().toLong())

                    val rand = ((10000000..90000000).random())
                    val cost = BigDecimal("0.00016$rand")
                    Log.d(TAG, "transaction output cost:-\n$cost")

                    val currentBalance =
                        BigDecimal(balance.value.toString())
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
                    viewModel.addressList.value = accList.value
                }
            }

            //------------------------------------------------------------------------has-user-voted
            checkVoterStatusButton.setOnClickListener {
                viewModel.alreadyVoted.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "voting status = ${hasUserVoted.value}")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.alreadyVoted.value = hasUserVoted.value.toString()
                    }
                }
            }
            //------------------------------------------------------------------get-voters-addresses
            getVotesButton.setOnClickListener {
                viewModel.allPartyVotes.value = calling
                val p1v = voteForPartyOne.value
                val p2v = voteForPartyTwo.value
                val p3v = voteForPartyThree.value
                CoroutineScope(Dispatchers.IO).launch {
                    val electionStatus =
                        "party 1 votes = " + p1v +
                                "\nparty 2 votes = " + p2v +
                                "\nparty 3 votes = " + p3v

                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.allPartyVotes.value = electionStatus
                    }
                }
            }

            getBalanceButton.setOnClickListener {
                viewModel.balance.value = calling
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep((1000..5000).random().toLong())
                    val balanceReceived = balance.value
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
                v1v2v3 = 1,
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
                acc = ACCOUT_ADDRESS
            )
        }
    }
}