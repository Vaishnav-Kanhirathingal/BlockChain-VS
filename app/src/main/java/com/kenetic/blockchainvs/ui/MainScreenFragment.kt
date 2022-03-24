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
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.kenetic.blockchainvs.R
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.app_viewmodel.MainViewModelFactory
import com.kenetic.blockchainvs.application_class.ApplicationStarter
import com.kenetic.blockchainvs.databinding.FragmentMainScreenBinding
import com.kenetic.blockchainvs.databinding.PromptLogOutBinding
import com.kenetic.blockchainvs.databinding.PromptLoginBinding
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
        //------------------------------------------------------------------------------------header
        val headerMenu = binding.navigationViewMainScreen.getHeaderView(0)
        val menuItemUserImage: ImageView = headerMenu.findViewById(R.id.user_image)
        val menuItemUserFullname: TextView = headerMenu.findViewById(R.id.user_full_name)
        val menuItemUserEmail: TextView = headerMenu.findViewById(R.id.user_email)
        val menuItemUserPhoneNumber: TextView = headerMenu.findViewById(R.id.user_phone_number)
        val menuItemUserAdharNumber: TextView = headerMenu.findViewById(R.id.user_adhar_card_number)
        val menuItemUserVoterId: TextView = headerMenu.findViewById(R.id.user_voter_id)

        // TODO: set image
        accountDataStore.userFullNameFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserFullname.text = it
        }
        accountDataStore.userEmailFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserEmail.text = it
        }
        accountDataStore.userContactNumberFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserPhoneNumber.text = it
        }
        accountDataStore.userAdharNoFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserAdharNumber.text = it
        }
        accountDataStore.userVoterIDFlow.asLiveData().observe(viewLifecycleOwner) {
            menuItemUserVoterId.text = it
        }
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
                R.id.contract_accessor -> {
//                    if (accountDataStore.userLoggedInFlow.asLiveData().value!!) {
//                        findNavController()
//                            .navigate(
//                                MainScreenFragmentDirections
//                                    .actionMainScreenFragmentToContractScreenFragment()
//                            )
//                    } else {
//                        Toast.makeText(
//                            requireContext(), "Log In Necessary To Continue", Toast.LENGTH_SHORT
//                        ).show()
//                        // TODO: this is for testing purposes only
//                        findNavController()
//                            .navigate(
//                                MainScreenFragmentDirections
//                                    .actionMainScreenFragmentToContractScreenFragment()
//                            )
//                        //loginPrompt()
//                    }

                        findNavController()
                            .navigate(
                                MainScreenFragmentDirections
                                    .actionMainScreenFragmentToContractScreenFragment()
                            )
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
                    logOutAndExit(true)
                    true
                }
                else -> {
                    throw IllegalArgumentException("side menu item not registered.")
                }
            }
        }
    }

    private fun switchAccount() {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.resetAccounts(requireContext())
        }
        loginPrompt()
    }

    private fun loginPrompt() {
        val promptLoginBinding = PromptLoginBinding.inflate(layoutInflater)
        val dialogBox = Dialog(requireContext())
        promptLoginBinding.apply {
            newUserButton.setOnClickListener {
                dialogBox.dismiss()
                findNavController().navigate(
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

    private fun logOutAndExit(exit:Boolean) {
        val dialogBox = Dialog(requireContext())
        val promptLogOutBinding = PromptLogOutBinding.inflate(layoutInflater)
        promptLogOutBinding.apply {
            logOut.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    accountDataStore.resetAccounts(requireContext())
                }
                if (exit){
                    // TODO: log out and exit
                }
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