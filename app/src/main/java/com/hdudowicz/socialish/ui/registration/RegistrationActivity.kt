package com.hdudowicz.socialish.ui.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
                        Snackbar.make(bindings.loginActivityContent, "Please connect to the internet.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is FirebaseAuthUserCollisionException -> {
                        Snackbar.make(bindings.loginActivityContent, "User with this email already exists", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toasty.error(this, "Please use a valid email").show()
                    }
                    else -> {
                        Snackbar.make(bindings.loginActivityContent, "Cant register user", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
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

}