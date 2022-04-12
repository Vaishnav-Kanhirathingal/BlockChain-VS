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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials

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
    private var privateKeyOk = false

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
                            phoneNumberOk = true
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
                //-----------------------------------------------------------------------private-key
                privateKeyOk = checkPrivateKeyOk(userPrivateKeyTextField.text.toString())
                //------------------------------------------------------sending-registration-details
                Log.d(
                    TAG,
                    "userNameOk = $userNameOk" +
                            "passwordOk = $passwordOk" +
                            "phoneNumberOk = $phoneNumberOk" +
                            "emailAddressOk = $emailAddressOk" +
                            "adharOk = $adharOk" +
                            "voterIdOk = $voterIdOk" +
                            "privateKeyOk = $privateKeyOk"
                )
                if (userNameOk && passwordOk && phoneNumberOk && emailAddressOk && adharOk && voterIdOk && privateKeyOk) {
                    val userName = userNameEditText.editText!!.text.toString()
                    val userPassword = userSetPasswordEditText.editText!!.text.toString()
                    val userPhoneNumber = userPhoneNumberEditText.editText!!.text.toString()
                    val userEmailID = userEmailEditText.editText!!.text.toString()
                    val adharNumber = adharCardNumber.editText!!.text.toString()
                    val voterID = voterId.editText!!.text.toString()
                    //--------------------------------------------------------creating-a-json-string
                    val stringToSend = "\"userName\" : \"$userName\",\n\t" +
                            "\"userPassword\": \"$userPassword\",\n\t" +
                            "\"userPhoneNumber\": \"$userPhoneNumber\",\n\t" +
                            "\"userEmailID\": \"$userEmailID\",\n\t" +
                            "\"adharNumber\": \"$adharNumber\",\n\t" +
                            "\"voterID\": \"$voterID\",\n\t" +
                            "\"publicKey\": \"${Credentials.create(userPrivateKeyTextField.text.toString()).address}\""
                    Log.d(TAG, "stringToSend\n$stringToSend")
                    val verification = MutableLiveData(false)
                    try {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            verification.value = VoteNetworkApi
//                                .retrofitService
//                                .sendAndVerifyUserDetails(stringToSend)
//                                .toBoolean()
//                        }
                        verification.value = true
                    } catch (e: Exception) {
                        verification.value = false
                    }
                    verification.observe(viewLifecycleOwner) {
                        //-----------------------------------------------------------store-user-data
                        if (it) {
                            saveToDataStore()
                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(
                                    SignUpFragmentDirections.actionSignUpFragmentToMainScreenFragment()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveToDataStore() {
        binding.apply {
            //-----------------------------------------------------------store-user-data
            CoroutineScope(Dispatchers.IO).launch {
                accountDataStore.apply {
                    //--------------------------------------------------------string
                    dataStoreStringSetter(
                        StringSetterEnum.USER_FULL_NAME_KEY,
                        userNameEditText.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_PASSWORD_KEY,
                        userConfirmPasswordEditText.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_EMAIL_KEY,
                        userEmailEditText.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_CONTACT_NUMBER_KEY,
                        userPhoneNumberEditText.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_VOTERS_ID_KEY,
                        voterId.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_ADHAR_NO_KEY,
                        adharCardNumber.editText!!.text.toString(),
                        requireContext()
                    )
                    dataStoreStringSetter(
                        StringSetterEnum.USER_PRIVATE_KEY_KEY,
                        userPrivateKeyTextField.text.toString(),
                        requireContext()
                    )
                    //-------------------------------------------------------boolean
                    dataStoreBooleanSetter(
                        BooleanSetterEnum.USER_USES_FINGERPRINT_KEY,
                        userUseFingerprint.isChecked,
                        requireContext()
                    )
                    dataStoreBooleanSetter(
                        BooleanSetterEnum.USER_LOGGED_IN,
                        true,
                        requireContext()
                    )
                    dataStoreBooleanSetter(
                        BooleanSetterEnum.USER_REGISTERED,
                        true,
                        requireContext()
                    )
                }
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
            val enteredPassword = userConfirmPasswordEditText.editText!!.text.toString()
            val confirmationPassword = userSetPasswordEditText.editText!!.text.toString()
            val passwordsMatch = (enteredPassword == confirmationPassword)
            Log.d(TAG, "enteredPassword = $enteredPassword")
            Log.d(TAG, " confirmationPassword = $confirmationPassword")

            userConfirmPasswordEditText.apply {
                error = "Passwords Do not Match"
                isErrorEnabled = !passwordsMatch
            }
            Log.d(TAG, "passwords match = $passwordsMatch")
            //-----------------------------------------------------------password-length-correct
            val passwordLengthCorrect = confirmationPassword.length in (9..25)
            userSetPasswordEditText.apply {
                error = "Password Length Should Be Between 8 to 24"
                isErrorEnabled = !passwordLengthCorrect
            }
            Log.d(TAG, "passwordLengthCorrect = $passwordLengthCorrect")
            return passwordsMatch && passwordLengthCorrect
        }
    }

    private fun checkPrivateKeyOk(privateKey: String): Boolean {
        val keyOk: Boolean = (privateKey.length == 64)
        binding.apply {
            userPrivateKey.error = "Incorrect Private Key"
            userPrivateKey.isErrorEnabled = !keyOk
        }
        return keyOk
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