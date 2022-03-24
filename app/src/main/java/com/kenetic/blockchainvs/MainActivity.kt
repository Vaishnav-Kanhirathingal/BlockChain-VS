package com.kenetic.blockchainvs

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.kenetic.blockchainvs.databinding.ActivityMainBinding
import com.kenetic.blockchainvs.databinding.PromptLogOutBinding
import com.kenetic.blockchainvs.datapack.datastore.AccountDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        navigationController =
            (supportFragmentManager.findFragmentById(R.id.nav_view) as NavHostFragment).navController
//        setupActionBarWithNavController(navigationController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val dialogBox = Dialog(baseContext)
        val promptLogOutBinding = PromptLogOutBinding.inflate(layoutInflater)
        promptLogOutBinding.apply {
            logOut.setOnClickListener {
                val accountDataStore = AccountDataStore(baseContext)
                CoroutineScope(Dispatchers.IO).launch {
                    accountDataStore.resetAccounts(baseContext)
                }
                // TODO: exit app
                super.onBackPressed()
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
            setCancelable(true)
            show()
        }
    }
}