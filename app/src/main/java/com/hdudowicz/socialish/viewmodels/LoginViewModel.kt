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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Email and pass variables updated using data binding in layout
    val emailText = ObservableField<String>()
    var passText = ObservableField<String>()


    private val mUserLoginState = MutableLiveData<Resource<FirebaseUser>>()
    val userLoginState: LiveData<Resource<FirebaseUser>> get() = mUserLoginState



    fun login() {
        // Using coroutines with IO dispatcher for network calls
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: Message if empty fields
                val person = LoginRepository.loginPerson(emailText.get().orEmpty(), passText.get().orEmpty())
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


    fun registerNewUser(username: String, email: String, pass: String): Task<AuthResult> {
        return LoginRepository.registerNewUser(email, pass)

    }

    override fun onCleared() {
        super.onCleared()

    }

}