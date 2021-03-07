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
import com.hdudowicz.socialish.data.model.User
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object LoginRepository {
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private val TAG = "LOGIN_REPO"

    fun login(email: String, pass: String): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful){
                    // Check current user updated
                    val fbUser = firebaseAuth.currentUser
                    if (fbUser != null) {
                        val user = User(uid = fbUser.uid,
                            email = fbUser.email!!,
                            name = fbUser.displayName!!
                        )
                        user.isAuthenticated = true

                        user.isNew = authTask.result!!.additionalUserInfo!!.isNewUser
                        userLiveData.postValue(user)
                    } else {
                        Log.e(TAG, "Login failed", authTask.exception)
                    }
                }
            }
        return userLiveData
    }

    fun registerNewUser(email: String, pass: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, pass)
    }
}