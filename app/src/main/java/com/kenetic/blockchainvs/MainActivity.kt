package com.kenetic.blockchainvs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.kenetic.blockchainvs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        navController = (binding.navView as NavHostFragment).navController
        setupActionBarWithNavController(navController)

//        val toolbar = binding.toolbarMainScreen
//        val drawerLayout = binding.root
//        val navigationView = binding.navigationViewMainScreen

//        setSupportActionBar(toolbar)
//
//        val toggle = ActionBarDrawerToggle(
//            this, drawerLayout, toolbar,
//            R.string.navigation_open,
//            R.string.navigation_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}