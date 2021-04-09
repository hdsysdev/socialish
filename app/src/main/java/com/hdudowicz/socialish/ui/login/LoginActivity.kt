package com.hdudowicz.socialish.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.ui.MainActivity
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityLoginBinding
import com.hdudowicz.socialish.ui.registration.RegistrationActivity
import com.hdudowicz.socialish.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityLoginBinding
    // Lazy load viewModel when it's first accessed
    private val viewModel by lazy {ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        setSupportActionBar(bindings.toolbar)


        bindings.viewModel = viewModel

        viewModel.userLoginState.observe(this, { resource ->
            if (resource is Resource.Success) {
                // Start main activity when user has logged in successfully
                startMainActivity()
            } else if (resource is Resource.Error) {
                // If exception is from invalid credentials, tell user to try again
                if (resource.exception is FirebaseAuthInvalidCredentialsException){
                    Snackbar.make(bindings.loginActivityContent, "Username or password is incorrect. Try again", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e("LOGIN", "login exception", resource.exception)
                    FirebaseCrashlytics.getInstance().recordException(resource.exception)
                }
            }
        })

        bindings.registerButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
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
                if (resultCode == Activity.RESULT_OK) {
                    startMainActivity()
                }
            }
        }
    }

}