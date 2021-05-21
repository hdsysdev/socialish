package com.hdudowicz.socialish.ui.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityRegistrationBinding
import com.hdudowicz.socialish.ui.MainActivity
import com.hdudowicz.socialish.viewmodels.RegistrationViewModel
import es.dmoral.toasty.Toasty
import java.lang.IllegalArgumentException
import java.net.ConnectException

class RegistrationActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityRegistrationBinding
    // Lazy load viewModel when it's first accessed
    private val viewModel by lazy {ViewModelProvider(this).get(RegistrationViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        setSupportActionBar(bindings.toolbar)

        bindings.viewModel = viewModel
        bindings.clickHandler = RegistrationClickHandler()

        viewModel.userRegisterState.observe(this, { resource ->
            if (resource is Resource.Success && resource.data != null) {
                // Start main activity when user has logged in successfully
                Toasty.success(this, "User registered").show()
                finish()
            } else if (resource is Resource.Error) {
                // If exception is from invalid credentials, tell user to try again
                FirebaseCrashlytics.getInstance().recordException(resource.exception)

                when (resource.exception) {
                    is ConnectException -> {
                        Toasty.warning(bindings.root.context, "Please connect to the internet").show()
                    }
                    is FirebaseAuthUserCollisionException -> {
                        Toasty.error(bindings.root.context, "User with this email already exists").show()

                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toasty.error(this, "Please use a valid email").show()
                    }
                    is IllegalArgumentException -> {
                        Toasty.error(this, "Please enter an email and password").show()
                    }
                    else -> {
                        Toasty.error(this, "Unexpected error").show()
                    }
                }
            }
            bindings.progressBar.visibility = View.GONE
        })
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

    inner class RegistrationClickHandler(){
        fun register(){
            if (bindings.emailText.text.isNullOrBlank() || bindings.passwordText.text.isNullOrBlank()){
                Toasty.error(bindings.root.context, "Enter a username and password").show()
            } else {
                bindings.progressBar.visibility = View.VISIBLE
                viewModel.registerNewUser()
            }
        }

    }
}