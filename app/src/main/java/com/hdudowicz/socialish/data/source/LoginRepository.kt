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

object LoginRepository {
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private const val TAG = "LOGIN_REPO"

    fun login(email: String, pass: String): LiveData<Person> {
        val userLiveData = MutableLiveData<Person>()

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful){
                    // Check current user updated
                    val fbUser = firebaseAuth.currentUser
                    if (fbUser != null) {
                        val user = Person(uid = fbUser.uid,
                            email = fbUser.email!!,
                            name = fbUser.displayName!!
                        )
                        user.isAuthenticated = true

                        user.isNew = authTask.result!!.additionalUserInfo!!.isNewUser
                        userLiveData.postValue(user)
                    } else {
                        Log.e(TAG, "Login failed: ", authTask.exception)
                    }
                }
            }
        return userLiveData
    }

    suspend fun loginPerson(email: String, pass: String): Resource<FirebaseUser> {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()

//        if (authResult.user != null) {
//            return Resource.Success(authResult.user)
//        } else {
//            return Resource.Error(NoSuchElementException("User not found"))
//        }
        return Resource.Success(authResult.user!!)
    }

    fun registerNewUser(email: String, pass: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, pass)
    }


}