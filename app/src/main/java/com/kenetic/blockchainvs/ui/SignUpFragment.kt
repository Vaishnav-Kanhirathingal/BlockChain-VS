package com.kenetic.blockchainvs.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.databinding.FragmentSignUpBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.BooleanSetterEnum
import com.kenetic.blockchainvs.datapack.datastore.StringSetterEnum
import com.kenetic.blockchainvs.networking_api.VoteNetworkApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var accountDataStore: AccountDataStore

    private var userNameOk = false
    private var passwordOk = false
    private var phoneNumberOk = false
    private var emailAddressOk = false
    private var adharOk = false
    private var voterIdOk = false
    private var walletOk = false
    private var phoneOtp: String? = null
    private var emailOtp: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountDataStore = AccountDataStore(requireContext())
        applyBindings()
    }

    private fun applyBindings() {
        CoroutineScope(Dispatchers.IO).launch {
            phoneNumberVerificationBinding()
            emailAccountVerification()
            registrationBinding()
            topMenuBinding()
            cancelBinding()
        }
    }

    private fun topMenuBinding() {
        binding.topAppBar.setNavigationOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                findNavController()
                    .navigate(SignUpFragmentDirections.actionSignUpFragmentToMainScreenFragment())
            }
        }
    }

    private fun phoneNumberVerificationBinding() {
        binding.apply {
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
                        //phoneOtp = VoteNetworkApi.retrofitService.getPhoneOtp(userPhoneNumberEditText.editText!!.text.toString())
                        // TODO: remove testing assignment
                        phoneOtp = "1111"
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
        }
    }

    private fun emailAccountVerification() {
        binding.apply {
            //------------------------------------------------------------email-account-verification
            userEmailOtpEditText.setEndIconOnClickListener {
                userEmailEditText.error = "Enter Email Correctly"
                if (userEmailEditText.editText!!.text.endsWith("@gmail.com")) {
                    Toast.makeText(requireContext(), "OTP Generating...", Toast.LENGTH_SHORT)
                        .show()
                    CoroutineScope(Dispatchers.IO).launch {
                        //emailOtp = VoteNetworkApi.retrofitService.getEmailOtp(userEmailEditText.editText!!.text.toString())
                        // TODO: remove testing assignment
                        emailOtp = "1111"
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
        }
    }

    private fun registrationBinding() {
        binding.apply {
            //--------------------------------------------------------------------------registration
            registerLogin.setOnClickListener {
                //------------------------------------------------------------full-name-verification
                userNameOk = (userNameEditText.editText!!.text.split(" ").size) == 3
                userNameEditText.isErrorEnabled = !userNameOk
                //--------------------------------------------------------------------password-match
                passwordOk = checkPasswordOk()
                //------------------------------------------------------------------------adhar-card
                adharOk = checkAdharOk()
                //--------------------------------------------------------------------------voter-id
                voterIdOk = checkVoterIdOk()
                //----------------------------------------------------------------------------wallet
                walletOk = checkWalletOk()
                Log.d(TAG, "wallet status = $walletOk")
                //------------------------------------------------------sending-registration-details
                if (userNameOk && passwordOk && phoneNumberOk && emailAddressOk && adharOk && voterIdOk && walletOk) {
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
        }
    }

    private fun checkWalletOk(): Boolean {
        binding.apply {
            val wallet = userWallet.editText!!.text.toString()
            val privateKey = userPrivateKey.editText!!.text.toString()

            val hostUrl = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
            val web3Obj = Web3j.build(HttpService(hostUrl))
            val credentials = Credentials.create(privateKey, wallet)
            return try {
                val balance = Convert.fromWei(
                    web3Obj
                        .ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                        .send().balance.toString(),
                    Convert.Unit.ETHER
                )
                Log.d(TAG, "balance = $balance")
                userWallet.isErrorEnabled = true
                userPrivateKey.isErrorEnabled = true
                true
            } catch (e: Exception) {
                e.printStackTrace()
                userWallet.error = "Wallet incorrect"
                userWallet.isErrorEnabled = true
                userPrivateKey.error = "PrivateKey incorrect"
                userPrivateKey.isErrorEnabled = true
                false
            }
        }
    }

    private fun checkAdharOk(): Boolean {
        binding.apply {
            (adharCardNumber.editText!!.text.toString().length == 12).let {
                adharCardNumber.apply {
                    error = "Enter Correct Adhar Number"
                    isErrorEnabled = !it
                }
                return it
            }
        }
    }

    private fun checkVoterIdOk(): Boolean {
        binding.apply {
            (voterId.editText!!.text.length == 10).let {
                voterId.apply {
                    error = "Enter Correct Voter ID"
                    isErrorEnabled = !it
                }
                return it
            }
        }
    }

    private fun checkPasswordOk(): Boolean {
        binding.apply {
            val passwordsMatch =
                userConfirmPasswordEditText.editText!!.text.toString() == userSetPasswordEditText.editText!!.text.toString()
            userConfirmPasswordEditText.apply {
                isErrorEnabled = !passwordsMatch
                error = "Passwords Do not Match"
            }
            //-----------------------------------------------------------password-length-correct
            val passwordLengthCorrect =
                userSetPasswordEditText.editText!!.text.toString().length in (9..25)
            userSetPasswordEditText.apply {
                isErrorEnabled = !passwordLengthCorrect
                error = "Password Length Should Be Between 8 to 24"
            }
            return passwordsMatch && passwordLengthCorrect
        }
    }

    private fun cancelBinding() {
        binding.apply {
            cancelLogin.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        requireContext(),
                        "Registration process cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToMainScreenFragment())
                }
            }
        }
    }
}