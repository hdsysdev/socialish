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

/**
 * The main activity of the app. Contains a bottom navigation bar that switches between fragments displayed in a NavHostFragment.
 *
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflating MainActivity views and setting the root view to the content view of the activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setting custom toolbar in layout as the activity support action bar
        setSupportActionBar(binding.topAppBar)

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