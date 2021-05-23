package com.hdudowicz.socialish.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.databinding.ActivityMainBinding
import com.hdudowicz.socialish.ui.createpost.CreatePostActivity
import com.hdudowicz.socialish.util.DialogUtil

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // Setting navController from fragment to be safe
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_feed, R.id.navigation_profile
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Setting up bottom navigation bar with navigation controller for the correct nav graph
        binding.navBar.setupWithNavController(navController)

    }

    /**
     * Overriding back button action to show a logout confirmation AlertDialog to the user instead of
     * closing the activity.
     */
    override fun onBackPressed() {
        DialogUtil.showLogoutDialog(this)
    }
}