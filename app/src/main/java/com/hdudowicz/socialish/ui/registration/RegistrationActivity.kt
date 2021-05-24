package com.hdudowicz.socialish.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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


/**
 * Activity for registering new user accounts for the application. Registers accounts using
 * Firebase Auth.
 *
 * @constructor Create a new registration activity
 */
class RegistrationActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityRegistrationBinding
    // Lazy load viewModel when it's first accessed
    private val viewModel by lazy {ViewModelProvider(this).get(RegistrationViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate activity views using generated binding class
        bindings = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        setSupportActionBar(bindings.toolbar)
        // Show back button in toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Sets the ViewModel and click handler classes for use with data binding in layouts
        bindings.viewModel = viewModel
        bindings.clickHandler = RegistrationClickHandler()

        // Observe the user's registration state. If successful they are automatically logged in.
        // If registering fails, an appropriate error message is shown to the user
        viewModel.userRegisterState.observe(this, { resource ->
            if (resource is Resource.Success && resource.data != null) {
                // Start main activity when user has logged in successfully
                Toasty.success(this, "User registered").show()
                finish()
                startMainActivity()
            } else if (resource is Resource.Error) {
                // If exception is from invalid credentials, tell user to try again
                FirebaseCrashlytics.getInstance().recordException(resource.exception)
                // Handle registration errors
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

    /**
     * Function to start the main activity
     */
    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Handle toolbar back button click action
     *
     * @return has the action been handled
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Click handler class for the RegistrationActivity
     *
     * @constructor Create a new click handler instance
     */
    inner class RegistrationClickHandler{
        /**
         * Registers a user with the entered credentials or if the email or text fields are empty then show an error
         */
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