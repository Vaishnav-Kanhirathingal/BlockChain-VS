package com.kenetic.blockchainvs.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding
import com.kenetic.blockchainvs.databinding.PromptLoginBinding
import com.kenetic.blockchainvs.databinding.PromptSignUpBinding
import com.kenetic.blockchainvs.databinding.SideMenuBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.BooleanSetterEnum
import com.kenetic.blockchainvs.recycler.PartyListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var partyAdapter: PartyListAdapter
    private lateinit var accountDataStore: AccountDataStore
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

        applyBinding()
    }

    private fun applyBinding() {
        accountDataStore = AccountDataStore(requireContext())
        partyAdapter = PartyListAdapter(viewModel)
        viewModel.getAllById().asLiveData().observe(viewLifecycleOwner) {
            partyAdapter.submitList(it)
        }
        binding.includedSubLayout.partyRecyclerView.adapter = partyAdapter
        applySideMenuBinding()
        applyTopMenuBindings()
    }

    private fun applyTopMenuBindings() {
        binding.apply {
            includedSubLayout.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.account_settings -> {
                        // TODO: new prompt to show log out and remove prompt
                        true
                    }
                    else -> {
                        throw IllegalArgumentException("top menu item not registered.")
                    }
                }
            }
        }
    }

    private fun applySideMenuBinding() {
        //------------------------------------------------------------------------------------header
        val headerBinding = SideMenuBinding.inflate(layoutInflater)
        headerBinding.apply {
            // TODO: set image
            accountDataStore.userFullNameFlow.asLiveData().observe(viewLifecycleOwner) {
                userFullName.text = it
            }
            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
                userEmail.text = it
            }
            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
                userPhoneNumber.text = it
            }
            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
                userAdharCardNumber.text = it
            }
            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
                userVoterId.text = it
            }
        }
        //-------------------------------------------------------------------------------bottom-menu
        binding.apply {
            navigationViewMainScreen.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.log_in -> {
                        // TODO: login prompt
                        true
                    }
                    R.id.log_out -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.resetAccounts(requireContext())
                        }
                        true
                    }
                    R.id.switch_account -> {
                        // TODO: remove present account and add new account
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.resetAccounts(requireContext())
                            loginPrompt()
                        }
                        true
                    }
                    R.id.about -> {
                        // TODO: navigate to about screen
                        true
                    }
                    R.id.exit -> {
                        // TODO: log out and close app
                        true
                    }
                    else -> {
                        throw IllegalArgumentException("side menu item not registered.")
                    }
                }
            }
        }
    }

    private fun logOutPrompt() {
        // TODO: add sign-out exit prompt
    }

    private fun loginPrompt() {
        val promptLoginBinding = PromptLoginBinding.inflate(layoutInflater)
        val dialogBox = Dialog(requireContext())
        promptLoginBinding.apply {
            newUserButton.setOnClickListener {
                dialogBox.dismiss()
                signupPrompt()
            }
            cancelLogin.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "An Account Is Necessary To Access Voting Services",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: quit app
            }
            confirmLogin.setOnClickListener {
                if (
                    (userNameEditText.editText!!.text.toString() == accountDataStore.userFullNameFlow.asLiveData().value!!.toString()) &&
                    (userSetPasswordEditText.editText!!.text.toString() == accountDataStore.userPasswordFlow.asLiveData().value!!)
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        accountDataStore.dataStoreBooleanSetter(
                            BooleanSetterEnum.USER_LOGGED_IN,
                            true,
                            requireContext()
                        )
                    }
                }
            }
            dialogBox.apply {
                setContentView(promptLoginBinding.root)
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setCancelable(false)
                show()
            }
        }
    }

    private fun signupPrompt() {
        val promptSignUpBinding = PromptSignUpBinding.inflate(layoutInflater)

        promptSignUpBinding.apply {
            verifyPhoneOtp.setOnClickListener {
                if (userPhoneNumberEditText.editText!!.text.length == 10) {
                    // TODO: if otp matches the otp sent, disable the edit text and the verify button
                } else {
                    Toast.makeText(requireContext(), "OTP Does Not Match", Toast.LENGTH_SHORT)
                        .show()
                    userPhoneOtpEditText.editText!!.setText("")
                }
            }
        }

        Dialog(requireContext()).apply {
        }
    }
}