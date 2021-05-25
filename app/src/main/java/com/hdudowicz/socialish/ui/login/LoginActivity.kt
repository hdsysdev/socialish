package com.hdudowicz.socialish.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityLoginBinding
import com.hdudowicz.socialish.ui.MainActivity
import com.hdudowicz.socialish.ui.registration.RegistrationActivity
import com.hdudowicz.socialish.viewmodels.LoginViewModel
import es.dmoral.toasty.Toasty
import java.net.ConnectException


/**
 * Activity containing login form, used to log into the user's app account or access the registration activity.
 *
 * @constructor Create a new login activity
 */
class LoginActivity : AppCompatActivity() {
    // Late initialised variable for activity view binding class
    private lateinit var bindings: ActivityLoginBinding
    // Lazy load viewModel for this LoginActivity when it is first accessed
    private val viewModel by lazy {ViewModelProvider(this).get(LoginViewModel::class.java) }

    /**
     * Overriding onCreate activity lifecycle callback.
     *
     * @param savedInstanceState Bundle object containing previous Activity state data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflating activity view using generated view binding class and assigning it to the bindings variable
        bindings = ActivityLoginBinding.inflate(layoutInflater)
        // Setting the content view of the activity to the root view in the view binding class
        setContentView(bindings.root)
        // Setting activity action bar to the one present in the activity layout
        setSupportActionBar(bindings.toolbar)

        // Setting ViewModel and ClickHandlers to use in the layout with data binding
        bindings.viewModel = viewModel
        bindings.clickHandler = LoginClickHandler()

        // Observing and handling user's login state. Successful login starts MainActivity.
        viewModel.userLoginState.observe(this, { resource ->
            if (resource is Resource.Success) {
                // Start main activity when user has logged in successfully
                startMainActivity()
            } else if (resource is Resource.Error) {
                // Handling different potential Firebase exceptions
                when (resource.exception) {
                                        // Show incorrect credential error shows a toast message asking the user to try again
                    is FirebaseAuthInvalidCredentialsException, is FirebaseAuthInvalidUserException -> {
                        Toasty.error(
                            bindings.root.context,
                            "Username or password is incorrect. Try again"
                        ).show()
                    }
                    // If not connected to the internet then display warning toast
                    is ConnectException -> {
                        Toasty.warning(bindings.root.context, "Please connect to the internet")
                            .show()
                    }
                    // If username or password are blank ask user to enter again
                    is IllegalArgumentException -> {
                        Toasty.error(bindings.root.context, "Enter a valid username and password")
                            .show()
                    }
                    else -> {
                        // Record unexpected exception to Firebase Crashlytics and show error toast
                        FirebaseCrashlytics.getInstance().recordException(resource.exception)
                        Toasty.error(bindings.root.context, "Unexpected error").show()
                    }
                }
            }
            // Hide loading spinner
            bindings.progressBar.visibility = View.GONE
        })

        // Check if a user is signed in and open main activity if so
        if(viewModel.isLoggedIn){
            startMainActivity()
        }
    }

    /**
     * Start the MainActivity when user is logged in
     */
    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Class for handling clicks on views in LoginActivity
     *
     * @constructor Create a new click handler object
     */
    inner class LoginClickHandler{
        /**
         * Login user if both email and password are entered, shows error if either field is blank
         *
         * @param view of the login button being clicked
         */
        fun login(view: View){
            if (bindings.emailText.text.isNullOrBlank() || bindings.passwordText.text.isNullOrBlank()){
                Toasty.error(view.context, "Enter a username and password").show()
            } else {
                bindings.progressBar.visibility = View.VISIBLE
                viewModel.login()
            }
        }

        /**
         * Start and display the registration activity
         *
         * @param view of the registration button being clicked
         */
        fun startRegistration(view: View){
            startActivity(Intent(view.context, RegistrationActivity::class.java))
        }
    }
}