package com.hdudowicz.socialish.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.data.source.LoginRepository
import com.hdudowicz.socialish.util.ConnectionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException

class LoginViewModel : ViewModel() {
    // Email and pass variables updated using data binding in layout
    val emailText = ObservableField<String>()
    var passText = ObservableField<String>()

    private val repository = LoginRepository()

    private val mUserLoginState = MutableLiveData<Resource<FirebaseUser>>()
    val userLoginState: LiveData<Resource<FirebaseUser>> get() = mUserLoginState

    val isLoggedIn get() = repository.isUserLoggedIn()

    fun login() {
        // Using coroutines with IO dispatcher for network calls
        viewModelScope.launch(Dispatchers.IO) {
            // Check if device is connected to the internet before trying to log in
            if(!ConnectionUtil.isInternetAvailable()){
                // If not connected to the internet then post exception to mUserLoginState
                mUserLoginState.postValue(Resource.Error(ConnectException()))
            } else {
                try {
                    // TODO: Message if empty fields
                    val person = repository.loginPerson(emailText.get().orEmpty(), passText.get().orEmpty())
                    mUserLoginState.postValue(person)
                } catch (exception: Exception) {

                    // Don't log exception if credentials wrong, post error response object with a message to show to the user
                    if (exception !is FirebaseAuthInvalidCredentialsException) {
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