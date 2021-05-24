package com.hdudowicz.socialish.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.data.source.RegistrationRepository
import com.hdudowicz.socialish.util.ConnectionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

/**
 * ViewModel class for the RegistrationActivity. Contains registration state and a function to register the new
 * user.
 *
 * @constructor Create new registration ViewModel
 */
class RegistrationViewModel : ViewModel() {
    // Email and pass variables updated using data binding in layout
    val emailText = ObservableField<String>()
    var passText = ObservableField<String>()

    // RegistrationRepository instance to register with Firebase Auth
    private val repository = RegistrationRepository()

    // LiveData returning the newly created FirebaseUser object or null wrapped in a Resource wrapper
    // containing any potential exceptions.
    private val mUserRegisterState = MutableLiveData<Resource<FirebaseUser?>>()
    val userRegisterState: LiveData<Resource<FirebaseUser?>> get() = mUserRegisterState

    /**
     * Registers a new user with firebase Auth and posts the resulting user or exception to the
     * userRegisterState LiveData
     */
    fun registerNewUser() {
        viewModelScope.launch(Dispatchers.IO) {
            // Check if device is connected to the internet before trying to log in
            if(!ConnectionUtil.isInternetAvailable()){
                // If not connected to the internet then post exception to mUserLoginState
                mUserRegisterState.postValue(Resource.Error(ConnectException()))
            } else {
                val newUser = repository.registerNewUser(emailText.get().orEmpty(), passText.get().orEmpty())

                if (newUser is Resource.Success){
                    mUserRegisterState.postValue(newUser)
                } else if (newUser is Resource.Error){
                    // Recording exception to Crashlytics
                    FirebaseCrashlytics.getInstance().recordException(newUser.exception)
                    mUserRegisterState.postValue(newUser)
                }
            }
        }
    }

}