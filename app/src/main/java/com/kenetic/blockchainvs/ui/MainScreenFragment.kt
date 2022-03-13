package com.kenetic.blockchainvs.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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

private const val TAG = "MainScreenFragment"

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
        applyTopMenuBindings()
        applySideMenuBinding()
    }

    private fun applyTopMenuBindings() {
        binding.apply {
            includedSubLayout.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.account_settings -> {
                        // TODO: new prompt to show log out and remove prompt
                        Log.d(TAG,"account settings now working from top menu")
                        loginPrompt()
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
        // TODO: fix functioning
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
                        Log.d(TAG,"log in working")
                        loginPrompt()
                        true
                    }
                    R.id.log_out -> {
                        Log.d(TAG,"log out working")
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.resetAccounts(requireContext())
                        }
                        true
                    }
                    R.id.switch_account -> {
                        // TODO: remove present account and add new account
                        Log.d(TAG,"switch account working")
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.resetAccounts(requireContext())
                            loginPrompt()
                        }
                        true
                    }
                    R.id.about -> {
                        // TODO: navigate to about screen
                        Log.d(TAG,"about working")
                        true
                    }
                    R.id.exit -> {
                        // TODO: log out and close app
                        Log.d(TAG,"exit working")
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
                    (userNameEditText.editText!!.text.toString() == accountDataStore.userFullNameFlow.asLiveData().value!!.toString())
                    &&
                    (userSetPasswordEditText.editText!!.text.toString() == accountDataStore.userPasswordFlow.asLiveData().value!!)
                    &&
                    (userNameEditText.editText!!.text.toString() != accountDataStore.default)
                    &&
                    (userSetPasswordEditText.editText!!.text.toString() != accountDataStore.default)
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
        val dialogBox = Dialog(requireContext())
        val promptSignUpBinding = PromptSignUpBinding.inflate(layoutInflater)

        promptSignUpBinding.apply {
            var userNameOk = false
            var passwordOk = false
            var phoneNumberOk = false
            var emailAddressOk = false
            var adharOk = false
            var voterIdOk = false
            //---------------------------------------------------------phone-number-verification
            userPhoneOtpEditText.setEndIconOnClickListener {
                userPhoneNumberEditText.error = "Enter Phone Number Correctly"
                if (
                    (userPhoneNumberEditText.editText!!.text.length in (10..14))
                ) {
                    Toast.makeText(requireContext(), "OTP Generating...", Toast.LENGTH_SHORT)
                        .show()
                    userPhoneNumberEditText.apply {
                        isErrorEnabled = false
                        isEnabled = false
                    }
                } else {
                    userPhoneNumberEditText.apply {
                        isErrorEnabled = true
                        isEnabled = true
                    }
                }
            }
            verifyPhoneOtp.setOnClickListener {
                userPhoneNumberEditText.isEnabled = false
                userPhoneOtpEditText.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    "Waiting For Verification...",
                    Toast.LENGTH_SHORT
                )
                    .show()
                // TODO: send OTP and wait for approval
            }
            clearPhoneOtp.setOnClickListener {
                phoneNumberOk = false
                userPhoneOtpEditText.apply {
                    editText!!.setText("")
                    isEnabled = true
                    isErrorEnabled = false
                }
                userPhoneNumberEditText.apply {
                    editText!!.setText("")
                    isEnabled = true
                    isErrorEnabled = false
                }
            }
            //--------------------------------------------------------email-account-verification
            userEmailOtpEditText.setEndIconOnClickListener {
                userEmailEditText.error = "Enter Email Correctly"
                if (userEmailEditText.editText!!.text.endsWith("@gmail.com")) {
                    Toast.makeText(requireContext(), "OTP Generating...", Toast.LENGTH_SHORT)
                        .show()
                    userEmailEditText.apply {
                        isErrorEnabled = false
                        isEnabled = false
                    }
                } else {
                    userEmailEditText.apply {
                        isErrorEnabled = true
                        isEnabled = true
                    }
                }
            }
            verifyEmailOtp.setOnClickListener {
                userEmailEditText.isEnabled = false
                userEmailOtpEditText.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    "Waiting For Verification...",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: send OTP and wait for approval
            }
            clearEmailOtp.setOnClickListener {
                emailAddressOk = false
                userEmailOtpEditText.apply {
                    editText!!.setText("")
                    isEnabled = true
                    isErrorEnabled = false
                }
                userEmailEditText.apply {
                    editText!!.setText("")
                    isEnabled = true
                    isErrorEnabled = false
                }
            }
            //--------------------------------------------------------------------------registration
            registerLogin.setOnClickListener {
                //------------------------------------------------------------full-name-verification
                userNameOk = (userNameEditText.editText!!.text.split(" ").size) == 3
                userNameEditText.isErrorEnabled = !userNameOk
                //--------------------------------------------------------------------password-match
                val passwordsMatch =
                    userConfirmPasswordEditText.editText!!.text == userSetPasswordEditText.editText!!.text
                userConfirmPasswordEditText.apply {
                    isErrorEnabled = passwordsMatch
                    error = "Passwords Do not Match"
                }
                //-----------------------------------------------------------password-length-correct
                val passwordLengthCorrect =
                    userSetPasswordEditText.editText!!.text.length in (9..25)
                userSetPasswordEditText.apply {
                    isErrorEnabled = passwordLengthCorrect
                    error = "Password Length Should Be Between 8 to 24"
                }
                passwordOk = passwordsMatch && passwordLengthCorrect
                //------------------------------------------------------------------------adhar-card
                (adharCardNumber.editText!!.text.length == 12).let {
                    adharCardNumber.apply {
                        error = "Enter Correct Adhar Number"
                        isErrorEnabled = !it
                    }
                    adharOk = it
                }
                //--------------------------------------------------------------------------voter-id
                (voterId.editText!!.text.length == 10).let {
                    voterId.apply {
                        error = "Enter Correct Voter ID"
                        isErrorEnabled = !it
                    }
                    voterIdOk = it
                }
                //------------------------------------------------------sending-registration-details
                if (userNameOk && passwordOk && phoneNumberOk && emailAddressOk && adharOk && voterIdOk) {
                    val userName = userNameEditText.editText!!.text.toString()
                    val userPassword = userSetPasswordEditText.editText!!.text.toString()
                    val userPhoneNumber = userPhoneNumberEditText.editText!!.text.toString()
                    val userEmailID = userEmailEditText.editText!!.text.toString()
                    val adharNumber = adharCardNumber.editText!!.text.toString()
                    val voterID = voterId.editText!!.text.toString()
                    // TODO: create a json string, send it and wait for registration if it fails, send a toast to try again. else, proceed
                }
            }
        }
        dialogBox.apply {
            setContentView(promptSignUpBinding.root)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setCancelable(false)
            show()
        }
    }
}