package com.hdudowicz.socialish.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.data.source.LoginRepository
import com.hdudowicz.socialish.data.source.RegistrationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    // Email and pass variables updated using data binding in layout
    val displayNameText = ObservableField<String>()
    val emailText = ObservableField<String>()
    var passText = ObservableField<String>()

    private val repository = RegistrationRepository()

    private val mUserRegisterState = MutableLiveData<Resource<FirebaseUser?>>()
    val userRegisterState: LiveData<Resource<FirebaseUser?>> get() = mUserRegisterState


    fun registerNewUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = repository.registerNewUser(emailText.get().orEmpty(), passText.get().orEmpty())

            if (newUser is Resource.Success){
                mUserRegisterState.postValue(newUser)
            } else if (newUser is Resource.Error){
                FirebaseCrashlytics.getInstance().recordException(newUser.exception)
                mUserRegisterState.postValue(newUser)
            }
        }
    }

}