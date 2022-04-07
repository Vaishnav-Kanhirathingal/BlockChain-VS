package com.kenetic.blockchainvs.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding
import com.kenetic.blockchainvs.databinding.PromptLogOutBinding
import com.kenetic.blockchainvs.databinding.PromptLoginBinding
import com.kenetic.blockchainvs.databinding.PromptSwitchBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import com.kenetic.blockchainvs.datapack.datastore.BooleanSetterEnum
import com.kenetic.blockchainvs.fingerprint.FingerPrintAuthentication
import com.kenetic.blockchainvs.fingerprint.FingerPrintTaskEnum
import com.kenetic.blockchainvs.recycler.TransactionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainScreenFragment"

class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var accountDataStore: AccountDataStore
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as ApplicationStarter).database.partyDao()
        )
    }

    private lateinit var userFullName: String
    private lateinit var userEmail: String
    private lateinit var userContactNumber: String
    private lateinit var userAdharNo: String
    private lateinit var userVoterID: String
    private var userLoggedIn: Boolean = false
    private lateinit var userPassword: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountDataStore = AccountDataStore(requireContext())
        applyMainLayoutBinding()
        applyTopMenuBindings()
        applySideMenuBinding()
    }

    private fun applyMainLayoutBinding() {
        transactionAdapter = TransactionAdapter(viewModel, viewLifecycleOwner, requireContext())
        viewModel.getAllById().asLiveData().observe(viewLifecycleOwner) {
            Log.d(TAG, "all ids = $it")
            transactionAdapter.submitList(it)
        }
        binding.includedSubLayout.partyRecyclerView.adapter = transactionAdapter
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun applyTopMenuBindings() {
        binding.apply {
            includedSubLayout.topAppBar.setOnMenuItemClickListener {
                Log.d(TAG, "top menu button working")
                when (it.itemId) {
                    R.id.account_settings -> {
                        if (userLoggedIn) {
                            switchAccountPrompt()
                        } else {
                            loginPrompt()
                        }
                        true
                    }
                    else -> {
                        throw IllegalArgumentException("top menu item not registered.")
                    }
                }
            }
            includedSubLayout.topAppBar.setNavigationOnClickListener {
                binding.root.openDrawer(binding.navigationViewMainScreen)
                Log.d(TAG, "setNavigationOnClickListener working")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun applySideMenuBinding() {
        //------------------------------------------------------------------------------------header
        val headerMenu = binding.navigationViewMainScreen.getHeaderView(0)
        val menuItemUserFullName: TextView = headerMenu.findViewById(R.id.user_full_name)
        val menuItemUserEmail: TextView = headerMenu.findViewById(R.id.user_email)
        val menuItemUserPhoneNumber: TextView = headerMenu.findViewById(R.id.user_phone_number)
        val menuItemUserAdharNumber: TextView = headerMenu.findViewById(R.id.user_adhar_card_number)
        val menuItemUserVoterId: TextView = headerMenu.findViewById(R.id.user_voter_id)
        val menuItemLoggedIn: TextView = headerMenu.findViewById(R.id.logged_in)

        //--------------------------------------------------------------------------data-store-saver
        accountDataStore.userFullNameFlow.asLiveData().observe(viewLifecycleOwner) {
            userFullName = it
            menuItemUserFullName.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            userEmail = it
            menuItemUserEmail.text = it
        }
        accountDataStore.userContactNumberFlow.asLiveData().observe(viewLifecycleOwner) {
            userContactNumber = it
            menuItemUserPhoneNumber.text = it
        }
        accountDataStore.userAdharNoFlow.asLiveData().observe(viewLifecycleOwner) {
            userAdharNo = it
            menuItemUserAdharNumber.text = it
        }
        accountDataStore.userVoterIDFlow.asLiveData().observe(viewLifecycleOwner) {
            userVoterID = it
            menuItemUserVoterId.text = it
        }
        accountDataStore.userLoggedInFlow.asLiveData().observe(viewLifecycleOwner) {
            userLoggedIn = it
            menuItemLoggedIn.text = if (it) {
                "Logged In"
            } else {
                "Not Logged In"
            }
        }
        //-----------------------------------------------------------------extra-data-store-elements
        accountDataStore.userPasswordFlow.asLiveData().observe(viewLifecycleOwner) {
            userPassword = it
        }
        accountDataStore.userUsesFingerprintFlow.asLiveData().observe(viewLifecycleOwner) {
            viewModel.userUsesFingerprint = it
        }
        //-------------------------------------------------------------------------------bottom-menu
        CoroutineScope(Dispatchers.IO).launch {
            binding.navigationViewMainScreen.setNavigationItemSelectedListener {
                Log.d(TAG, "navigationViewMainScreen working")
                when (it.itemId) {
                    R.id.log_in -> {
                        loginPrompt()
                        true
                    }
                    R.id.log_out -> {
                        Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT)
                            .show()
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.logOut(requireContext())
                        }
                        true
                    }
                    R.id.sign_up -> {
                        findNavController()
                            .navigate(
                                MainScreenFragmentDirections.actionMainScreenFragmentToSignUpFragment()
                            )
                        true
                    }
                    R.id.remove_account -> {
                        Toast.makeText(requireContext(), "Account Removed", Toast.LENGTH_SHORT)
                            .show()
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.resetAccounts(requireContext())
                        }
                        true
                    }
                    R.id.switch_account -> {
                        switchAccountPrompt()
                        true
                    }
                    R.id.contract_interface -> {
                        if (userLoggedIn) {
                            findNavController()
                                .navigate(
                                    MainScreenFragmentDirections
                                        .actionMainScreenFragmentToContractScreenFragment()
                                )
                        } else {
                            Toast.makeText(
                                requireContext(), "Log In Necessary To Continue", Toast.LENGTH_SHORT
                            ).show()
                            loginPrompt()
                        }
                        true
                    }
                    R.id.about -> {
                        findNavController()
                            .navigate(
                                MainScreenFragmentDirections
                                    .actionMainScreenFragmentToAboutFragment()
                            )
                        Log.d(TAG, "about working")
                        true
                    }
                    R.id.exit -> {
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
    }

    private fun switchAccountPrompt() {
        val promptSwitchBinding = PromptSwitchBinding.inflate(layoutInflater)
        val dialogBox = Dialog(requireContext())
        promptSwitchBinding.apply {
            cancel.setOnClickListener {
                dialogBox.dismiss()
            }
            confirm.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    accountDataStore.resetAccounts(requireContext())
                }
                findNavController()
                    .navigate(
                        MainScreenFragmentDirections
                            .actionMainScreenFragmentToSignUpFragment()
                    )
                dialogBox.dismiss()
            }
            currentAccountFullName.text = userFullName
        }
        dialogBox.apply {
            setContentView(promptSwitchBinding.root)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun loginPrompt() {
        val promptLoginBinding = PromptLoginBinding.inflate(layoutInflater)
        val dialogBox = Dialog(requireContext())
        promptLoginBinding.apply {
            newUserButton.setOnClickListener {
                dialogBox.dismiss()
                findNavController()
                    .navigate(
                        MainScreenFragmentDirections.actionMainScreenFragmentToSignUpFragment()
                    )
            }
            cancelLogin.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "An Account Is Necessary To Access Voting Services",
                    Toast.LENGTH_SHORT
                ).show()
                dialogBox.dismiss()
            }
            confirmLogin.setOnClickListener {
                if (
                    (userNameEditText.editText!!.text.toString() == userFullName)
                    &&
                    (userSetPasswordEditText.editText!!.text.toString() == userPassword)
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
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast
                                .makeText(requireContext(), "Logged In", Toast.LENGTH_SHORT)
                                .show()
                            dialogBox.dismiss()
                        }
                    }
                }
            }
            //---------------------------------------------------------------------------fingerprint
            userNameEditText.setEndIconOnClickListener {
                if (viewModel.userUsesFingerprint) {
                    FingerPrintAuthentication(requireContext(), FingerPrintTaskEnum.LOGIN) {
                        CoroutineScope(Dispatchers.IO).launch {
                            accountDataStore.dataStoreBooleanSetter(
                                BooleanSetterEnum.USER_LOGGED_IN,
                                true,
                                requireContext()
                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast
                                    .makeText(requireContext(), "Logged In", Toast.LENGTH_SHORT)
                                    .show()
                                dialogBox.dismiss()
                            }
                        }
                    }.verifyBiometrics()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Fingerprint Login Disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialogBox.dismiss()
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

    private fun logOutAndExit() {
        val dialogBox = Dialog(requireContext())
        val promptLogOutBinding = PromptLogOutBinding.inflate(layoutInflater)
        promptLogOutBinding.apply {
            logOut.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    accountDataStore.resetAccounts(requireContext())
                }
                ActivityCompat.finishAffinity(requireActivity())
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