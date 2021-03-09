package com.hdudowicz.socialish.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.hdudowicz.socialish.MainActivity
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityLoginBinding
import com.hdudowicz.socialish.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    // Lazy load viewModel when it's first accessed
    private val viewModel by lazy {ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)


        bind.viewModel = viewModel

        viewModel.userLoginState.observe(this, { resource ->
            if (resource is Resource.Success) {
                // Start main activity when user has logged in successfully
                startMainActivity()
            } else if (resource is Resource.Error) {
                // If exception is from invalid credentials, tell user to try again
                if (resource.exception is FirebaseAuthInvalidCredentialsException){
                    Snackbar.make(bind.loginActivityContent, "Username or password is incorrect. Try again", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e("AUTH", "onCreate: ", resource.exception)
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
        // Check if a user is signed in and open main activity if so
        if(viewModel.isLoggedIn){
            startMainActivity()
        }

    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            0 -> {
                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    startMainActivity()
                } else {
                    // Signin failed
                }
            }
        }
    }

}