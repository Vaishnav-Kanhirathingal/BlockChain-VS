package com.kenetic.blockchainvs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.kenetic.blockchainvs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.kenetic.blockchainvs.databinding.ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        navController =(supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
//        setupActionBarWithNavController(navController)
    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}