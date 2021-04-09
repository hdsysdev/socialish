package com.hdudowicz.socialish.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdudowicz.socialish.data.model.Person
import com.hdudowicz.socialish.data.model.Resource
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginRepository {
    private var firebaseAuth: FirebaseAuth = Firebase.auth

    suspend fun loginPerson(email: String, pass: String): Resource<FirebaseUser> {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()

//        if (authResult.user != null) {
//            return Resource.Success(authResult.user)
//        } else {
//            return Resource.Error(NoSuchElementException("User not found"))
//        }
        // TODO: Check if user successfully logged in
        return Resource.Success(authResult.user!!)
    }

    suspend fun registerNewUser(email: String, pass: String): Resource<FirebaseUser?> {
        val registerStatus = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()

        return Resource.Success(registerStatus.user)
    }

    fun isUserLoggedIn(): Boolean{
        return firebaseAuth.currentUser != null
    }


}