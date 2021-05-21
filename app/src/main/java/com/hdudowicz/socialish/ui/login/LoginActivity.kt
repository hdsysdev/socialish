package com.hdudowicz.socialish.ui.login

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.ui.MainActivity
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityLoginBinding
import com.hdudowicz.socialish.ui.registration.RegistrationActivity
import com.hdudowicz.socialish.util.ConnectionUtil
import com.hdudowicz.socialish.viewmodels.LoginViewModel
import es.dmoral.toasty.Toasty
import java.lang.IllegalArgumentException
import java.net.ConnectException

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
        bindings.clickHandler = LoginClickHandler()

        viewModel.userLoginState.observe(this, { resource ->
            if (resource is Resource.Success) {
                // Start main activity when user has logged in successfully
                startMainActivity()
            } else if (resource is Resource.Error) {

                // If exception is from invalid credentials, tell user to try again
                when (resource.exception) {
                    is FirebaseAuthInvalidCredentialsException, is FirebaseAuthInvalidUserException -> {
                        Toasty.error(bindings.root.context, "Username or password is incorrect. Try again").show()
                    }
                    is ConnectException -> {
                        Toasty.warning(bindings.root.context, "Please connect to the internet").show()
                    }
                    is IllegalArgumentException -> {
                        Toasty.error(bindings.root.context, "Enter a valid username and password").show()
                    }
                    else -> {
                        Log.e("LOGIN", "login exception", resource.exception)
                        // Record unexpected exception to Firebase Crashlytics
                        FirebaseCrashlytics.getInstance().recordException(resource.exception)
                        Toasty.error(bindings.root.context, "Unexpected error").show()
                    }
                }
            }
            bindings.progressBar.visibility = View.GONE
        })
    }

    override fun onStart() {
        super.onStart()
        // Check if a user is signed in and open main activity if so
        if(viewModel.isLoggedIn){
            startMainActivity()
        }
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

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    inner class LoginClickHandler(){
        fun login(){
            if (bindings.emailText.text.isNullOrBlank() || bindings.passwordText.text.isNullOrBlank()){
                Toasty.error(bindings.root.context, "Enter a username and password").show()
            } else {
                bindings.progressBar.visibility = View.VISIBLE
                viewModel.login()
            }
        }
        fun startRegistration(){
            startActivity(Intent(baseContext, RegistrationActivity::class.java))
        }
    }
}