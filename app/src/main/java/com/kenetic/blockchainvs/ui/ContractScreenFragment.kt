package com.kenetic.blockchainvs.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.contract_classes.PartyEnum
import com.kenetic.blockchainvs.contract_classes.VoteContractDelegate
import com.kenetic.blockchainvs.databinding.FragmentContractScreenBinding
import com.kenetic.blockchainvs.databinding.PromptPasswordBinding
import com.kenetic.blockchainvs.databinding.PromptTransactionReceiptBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.fingerprint.FingerPrintAuthentication
import com.kenetic.blockchainvs.fingerprint.FingerPrintTaskEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.protocol.core.methods.response.TransactionReceipt

private const val TAG = "ContractScreenFragment"

class ContractScreenFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as ApplicationStarter).database.partyDao())
    }

    private lateinit var binding: FragmentContractScreenBinding
    private var partyEnum = PartyEnum.ONE
    private lateinit var voteContractDelegate: VoteContractDelegate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContractScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voteContractDelegate = VoteContractDelegate(viewModel)
        applyBinding()
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
                fun(_: RadioGroup, i: Int) {
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
                val task = {
                    viewModel.transactionCost.value = viewModel.transactionInProgress
                    CoroutineScope(Dispatchers.IO).launch {
                        val transactionReceipt = voteContractDelegate.registerVote(partyEnum)
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.transactionCost.value =
                                viewModel.gasUsedIs + transactionReceipt!!.gasUsed
                            transactionPrompt(transactionReceipt)
                        }
                    }
                }
                if (viewModel.userUsesFingerprint) {
                    Log.d(TAG, "fingerprint prompt initiated")
                    val authenticator = FingerPrintAuthentication(
                        requireContext(),
                        FingerPrintTaskEnum.TRANSACTION
                    ) { task() }
                    authenticator.verifyBiometrics()
                } else {
                    Log.d(TAG, "password prompt initiated")
                    passwordPrompt { task() }
                }
            }
            addToVotersListButton.setOnClickListener {
                val task = {
                    viewModel.addMeToVotersList.value = viewModel.calling
                    CoroutineScope(Dispatchers.IO).launch {
                        val transactionReceipt = voteContractDelegate.addMeToVotedList()
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.addMeToVotersList.value =
                                viewModel.gasUsedIs + transactionReceipt!!.gasUsed.toString()
                            transactionPrompt(transactionReceipt)
                        }
                    }
                }
                if (viewModel.userUsesFingerprint) {
                    Log.d(TAG, "fingerprint prompt initiated")
                    val authenticator = FingerPrintAuthentication(
                        requireContext(),
                        FingerPrintTaskEnum.TRANSACTION
                    ) { task() }
                    authenticator.verifyBiometrics()
                } else {
                    Log.d(TAG, "password prompt initiated")
                    passwordPrompt { task() }
                }
            }

            //----------------------------------------------------------------------------get-voters
            getRegisteredVotersButton.setOnClickListener {
                viewModel.addressList.value = viewModel.calling
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
                viewModel.alreadyVoted.value = viewModel.calling
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
                viewModel.allPartyVotes.value = viewModel.calling
                CoroutineScope(Dispatchers.IO).launch {
                    val electionStatus = voteContractDelegate.partyVotesStatus()
                    Log.d(TAG, "election status received: \n$electionStatus")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.allPartyVotes.value = electionStatus
                    }
                }
            }

            getBalanceButton.setOnClickListener {
                viewModel.balance.value = viewModel.calling
                CoroutineScope(Dispatchers.IO).launch {
                    val balanceReceived = voteContractDelegate.getBalance()
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.balance.value = "$balanceReceived ETH"
                    }
                }
            }
        }
    }

    private fun transactionPrompt(transactionReceipt: TransactionReceipt) {
        transactionReceipt.effectiveGasPrice = "${voteContractDelegate.gweiPrice} Gwei"
        val dialogBox = Dialog(requireContext())
        val promptBinding = PromptTransactionReceiptBinding.inflate(layoutInflater)
        promptBinding.apply {
            transactionHashTextView.text = transactionReceipt.transactionHash
            transactionIndexTextView.text = transactionReceipt.transactionIndex.toString()
            isStatusOKTextView.text = transactionReceipt.isStatusOK.toString()
            revertReasonTextView.text = transactionReceipt.revertReason
            blockNumberTextView.text = transactionReceipt.blockNumber.toString()
            fromTextView.text = transactionReceipt.from
            toTextView.text = transactionReceipt.to
            effectiveGasPriceTextView.text = transactionReceipt.effectiveGasPrice
            gasUsedTextView.text = transactionReceipt.gasUsed.toString()
            dismissButton.setOnClickListener { dialogBox.dismiss() }
        }
        dialogBox.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(promptBinding.root)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
            show()
        }
    }

    private fun passwordPrompt(task: () -> Unit) {
        val dialogBox = Dialog(requireContext())
        val promptBinding = PromptPasswordBinding.inflate(layoutInflater)
        promptBinding.apply {
            cancel.setOnClickListener {
                dialogBox.dismiss()
            }
            confirm.setOnClickListener {
                if (passwordTextField.text.toString() == AccountDataStore(requireContext()).userPasswordFlow.asLiveData().value.toString()) {
                    task()
                    dialogBox.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Task Is Being Executed",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Password Incorrect, Try Again Later",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialogBox.dismiss()
                }
            }
        }
        dialogBox.apply {
            setContentView(promptBinding.root)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
            show()
        }
    }
}