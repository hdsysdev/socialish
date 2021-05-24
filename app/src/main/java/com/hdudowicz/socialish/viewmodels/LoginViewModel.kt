package com.hdudowicz.socialish.viewmodels

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.data.source.LoginRepository
import com.hdudowicz.socialish.util.ConnectionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

/**
 * ViewModel class for the LoginActivity. Stores entered email and password text and handles logging
 * in using Firebase Auth through the LoginRepository.
 *
 * @constructor Create new Login ViewModel
 */
class LoginViewModel : ViewModel() {
    // Observable email and pass variables automatically updated using data binding in layout
    val emailText = ObservableField<String>()
    var passText = ObservableField<String>()

    // Login repository instance to make network calls to Firebase Auth
    private val repository = LoginRepository()

    // LiveData to notify the Activity of the user's login state. Contains a FirebaseUser object wrapped in a
    // wrapper class containing the result of a network operation or an exception upon failure
    private val mUserLoginState = MutableLiveData<Resource<FirebaseUser>>()
    val userLoginState: LiveData<Resource<FirebaseUser>> get() = mUserLoginState

    // Getter to get user's logged in status
    val isLoggedIn get() = repository.isUserLoggedIn()

    /**
     * Function launching a coroutine in the ViewModel's scope. First checks if internet is available
     * and posts an error to the mUserLoginState LiveData if not. Then tries to log the user in
     * with the provided credentials and posts the result for handling in the Activity
     */
    fun login() {
        // Using coroutines with IO dispatcher for network calls
        viewModelScope.launch(Dispatchers.IO) {
            // Check if device is connected to the internet before trying to log in
            if(!ConnectionUtil.isInternetAvailable()){
                // If not connected to the internet then post exception to mUserLoginState
                mUserLoginState.postValue(Resource.Error(ConnectException()))
            } else {
                try {

                    val person = repository.loginPerson(emailText.get().orEmpty(), passText.get().orEmpty())
                    mUserLoginState.postValue(person)
                } catch (exception: Exception) {

                    // Don't log exception if credentials wrong, post error response object with a message to show to the user
                    if (exception !is FirebaseAuthInvalidCredentialsException ||
                        exception !is FirebaseAuthInvalidUserException) {
                        // Logging other exceptions with crashlytics
                        FirebaseCrashlytics.getInstance().log("Error logging in user " + emailText)
                        FirebaseCrashlytics.getInstance().recordException(exception)
                    }
                    mUserLoginState.postValue(Resource.Error(exception))
                }
            }
        }
    }
}