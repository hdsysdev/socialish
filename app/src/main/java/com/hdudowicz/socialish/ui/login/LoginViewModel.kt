package com.hdudowicz.socialish.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.hdudowicz.socialish.data.model.User
import com.hdudowicz.socialish.data.source.LoginRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val authUser = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = authUser

    fun login(email: String, pass: String): AuthResult? {
        LoginRepository.login(email, pass)


    }

    fun registerNewUser(username: String, email: String, pass: String): AuthResult {
        return LoginRepository.registerNewUser(email, pass)

    }

    override fun onCleared() {
        super.onCleared()

    }

}