package com.kenetic.blockchainvs.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding
import com.kenetic.blockchainvs.databinding.PromptLogOutBinding
import com.kenetic.blockchainvs.databinding.PromptLoginBinding
import com.kenetic.blockchainvs.databinding.PromptSignUpBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.BooleanSetterEnum
import com.kenetic.blockchainvs.datapack.datastore.StringSetterEnum
import com.kenetic.blockchainvs.networking_api.VoteNetworkApi
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

        applyMainLayoutBinding()
        applyTopMenuBindings()
        applySideMenuBinding()
    }

    private fun applyMainLayoutBinding() {
        accountDataStore = AccountDataStore(requireContext())
        partyAdapter = PartyListAdapter(viewModel)
        viewModel.getAllById().asLiveData().observe(viewLifecycleOwner) {
            partyAdapter.submitList(it)
        }
        binding.includedSubLayout.partyRecyclerView.adapter = partyAdapter
    }

    private fun applyTopMenuBindings() {
        binding.apply {
            includedSubLayout.topAppBar.setOnMenuItemClickListener {
                Log.d(TAG, "top menu button working")
                when (it.itemId) {
                    R.id.account_settings -> {
                        // TODO: new prompt to show log out and remove prompt
                        loginPrompt()
                        true
                    }
                    else -> {
                        throw IllegalArgumentException("top menu item not registered.")
                    }
                }
            }
            includedSubLayout.topAppBar.setNavigationOnClickListener {
                //working
                Log.d(TAG, "setNavigationOnClickListener working")
            }
        }
    }

    private fun applySideMenuBinding() {
        // TODO: fix functioning for both header and menu
        //-----------------------------------------------------------------------------------testing
        //----------------------------------------------------------------------------header-testing
        val headerMenu = binding.navigationViewMainScreen.getHeaderView(0)
        val menuItemUserImage: ImageView = headerMenu.findViewById(R.id.user_image)
        val menuItemUserFullname: TextView = headerMenu.findViewById(R.id.user_full_name)
        val menuItemUserEmail: TextView = headerMenu.findViewById(R.id.user_email)
        val menuItemUserPhoneNumber: TextView = headerMenu.findViewById(R.id.user_phone_number)
        val menuItemUserAdharNumber: TextView = headerMenu.findViewById(R.id.adhar_card_number)
        val menuItemUserVoterId: TextView = headerMenu.findViewById(R.id.voter_id)

        // TODO: set image
        accountDataStore.userFullNameFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserFullname.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserEmail.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserPhoneNumber.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserAdharNumber.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserVoterId.text = it
        }
        //------------------------------------------------------------------------------menu-testing
        val subMenu = binding.navigationViewMainScreen.menu

        //------------------------------------------------------------------------------------header
//
//        val headerBinding = SideMenuBinding.inflate(layoutInflater)
//        headerBinding.apply {
//            // TODO: set image
//            accountDataStore.userFullNameFlow.asLiveData().observe(viewLifecycleOwner) {
//                userFullName.text = it
//            }
//            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
//                userEmail.text = it
//            }
//            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
//                userPhoneNumber.text = it
//            }
//            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
//                userAdharCardNumber.text = it
//            }
//            accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
//                userVoterId.text = it
//            }
//        }
        //-------------------------------------------------------------------------------bottom-menu
        binding.navigationViewMainScreen.setNavigationItemSelectedListener {
            Log.d(TAG, "navigationViewMainScreen working")
            when (it.itemId) {
                R.id.log_in -> {
                    Log.d(TAG, "log in working")
                    loginPrompt()
                    true
                }
                R.id.log_out -> {
                    Log.d(TAG, "log out working")
                    CoroutineScope(Dispatchers.IO).launch {
                        accountDataStore.resetAccounts(requireContext())
                    }
                    true
                }
                R.id.switch_account -> {
                    // TODO: remove present account and add new account
                    Log.d(TAG, "switch account working")
                    switchAccount()
                    true
                }
                R.id.about -> {
                    // TODO: navigate to about screen
                    Log.d(TAG, "about working")
                    true
                }
                R.id.exit -> {
                    // TODO: log out and close app
                    Log.d(TAG, "exit working")
                    logOutAndExit()
                    true
                }
                else -> {
                    throw IllegalArgumentException("side menu item not registered.")
                }
            }
        }
    }

    private fun logOutPrompt() {
        // TODO: add sign-out exit prompt
    }

    private fun switchAccount() {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.resetAccounts(requireContext())
            loginPrompt()
        }
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
                dialogBox.dismiss()
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
            var phoneOtp: String? = null
            var emailOtp: String? = null
            //-------------------------------------------------------------phone-number-verification
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
                    CoroutineScope(Dispatchers.IO).launch {
                        phoneOtp = VoteNetworkApi
                            .retrofitService
                            .getPhoneOtp(userPhoneNumberEditText.editText!!.text.toString())
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
                    when (phoneOtp) {
                        null -> {
                            userPhoneOtpEditText.isEnabled = true
                            "OTP Not Generated Yet"
                        }
                        userPhoneOtpEditText.editText!!.text.toString() -> {
                            emailAddressOk = true
                            userPhoneOtpEditText.isEnabled = false
                            "OTP Verified"
                        }
                        else -> {
                            userPhoneOtpEditText.isEnabled = true
                            "Email Address Not Verified, Wrong OTP"
                        }
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }
            clearPhoneOtp.setOnClickListener {
                phoneNumberOk = false
                phoneOtp = null
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
            //------------------------------------------------------------email-account-verification
            userEmailOtpEditText.setEndIconOnClickListener {
                userEmailEditText.error = "Enter Email Correctly"
                if (userEmailEditText.editText!!.text.endsWith("@gmail.com")) {
                    Toast.makeText(requireContext(), "OTP Generating...", Toast.LENGTH_SHORT)
                        .show()
                    CoroutineScope(Dispatchers.IO).launch {
                        emailOtp =
                            VoteNetworkApi
                                .retrofitService
                                .getEmailOtp(userEmailEditText.editText!!.text.toString())
                    }
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
                    when (emailOtp) {
                        null -> {
                            userEmailOtpEditText.isEnabled = true
                            "OTP Not Generated Yet"
                        }
                        userEmailOtpEditText.editText!!.text.toString() -> {
                            emailAddressOk = true
                            userEmailOtpEditText.isEnabled = false
                            "OTP Verified"
                        }
                        else -> {
                            userEmailOtpEditText.isEnabled = true
                            "Email Address Not Verified, Wrong OTP"
                        }
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }
            clearEmailOtp.setOnClickListener {
                emailAddressOk = false
                emailOtp = null
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
                    userConfirmPasswordEditText.editText!!.text.toString() == userSetPasswordEditText.editText!!.text.toString()
                userConfirmPasswordEditText.apply {
                    isErrorEnabled = passwordsMatch
                    error = "Passwords Do not Match"
                }
                //-----------------------------------------------------------password-length-correct
                val passwordLengthCorrect =
                    userSetPasswordEditText.editText!!.text.toString().length in (9..25)
                userSetPasswordEditText.apply {
                    isErrorEnabled = passwordLengthCorrect
                    error = "Password Length Should Be Between 8 to 24"
                }
                passwordOk = passwordsMatch && passwordLengthCorrect
                //------------------------------------------------------------------------adhar-card
                (adharCardNumber.editText!!.text.toString().length == 12).let {
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
                    //--------------------------------------------------------creating-a-json-string
                    val stringToSend = "\"userName\" : \"$userName\",\n" +
                            "\"userPassword\": \"$userPassword\",\n" +
                            "\"userPhoneNumber\": \"$userPhoneNumber\",\n" +
                            "\"userEmailID\": \"$userEmailID\",\n" +
                            "\"adharNumber\": \"$adharNumber\",\n" +
                            "\"voterID\": \"$voterID\""
                    Log.d(TAG, stringToSend)
                    val verification = MutableLiveData(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        verification.value = VoteNetworkApi
                            .retrofitService
                            .sendAndVerifyUserDetails(stringToSend)
                            .toBoolean()
                    }
                    verification.observe(viewLifecycleOwner) {
                        //-----------------------------------------------------------store-user-data
                        if (it) {
                            CoroutineScope(Dispatchers.IO).launch {
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_FULL_NAME_KEY,
                                    userNameEditText.editText!!.text.toString(),
                                    requireContext()
                                )
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_PASSWORD_KEY,
                                    userConfirmPasswordEditText.editText!!.text.toString(),
                                    requireContext()
                                )
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_EMAIL_KEY,
                                    userEmailEditText.editText!!.text.toString(),
                                    requireContext()
                                )
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_CONTACT_NUMBER_KEY,
                                    userPhoneNumberEditText.editText!!.text.toString(),
                                    requireContext()
                                )
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_VOTERS_ID_KEY,
                                    voterId.editText!!.text.toString(),
                                    requireContext()
                                )
                                accountDataStore.dataStoreStringSetter(
                                    StringSetterEnum.USER_ADHAR_NO_KEY,
                                    adharCardNumber.editText!!.text.toString(),
                                    requireContext()
                                )

                                accountDataStore.dataStoreBooleanSetter(
                                    BooleanSetterEnum.USER_USES_FINGERPRINT_KEY,
                                    userUseFingerprint.isChecked,
                                    requireContext()
                                )
                                accountDataStore.dataStoreBooleanSetter(
                                    BooleanSetterEnum.USER_LOGGED_IN,
                                    true,
                                    requireContext()
                                )
                                accountDataStore.dataStoreBooleanSetter(
                                    BooleanSetterEnum.USER_REGISTERED,
                                    true,
                                    requireContext()
                                )
                            }
                        }
                    }
                }
            }
            cancelLogin.setOnClickListener {
                dialogBox.dismiss()
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

    private fun logOutAndExit() {
        val dialogBox = Dialog(requireContext())
        val promptLogOutBinding = PromptLogOutBinding.inflate(layoutInflater)
        promptLogOutBinding.apply {
            logOut.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    accountDataStore.resetAccounts(requireContext())
                }
                // TODO: log out and exit
            }
            cancel.setOnClickListener {
                dialogBox.dismiss()
            }
        }
        dialogBox.apply {
            setContentView(promptLogOutBinding.root)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
            show()
        }
    }
}