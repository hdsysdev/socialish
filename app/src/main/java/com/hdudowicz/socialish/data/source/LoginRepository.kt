package com.hdudowicz.socialish.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdudowicz.socialish.data.model.Resource
import kotlinx.coroutines.tasks.await

/**
 * Repository for the LoginActivity containing functions for logging in user into Firebase Auth.
 *
 * @constructor Create login repository
 */
class LoginRepository {
    private var firebaseAuth: FirebaseAuth = Firebase.auth

    /**
     * Suspending function to login user using the passed Firebase Auth credentials.
     * Returns firebase user is no error was thrown. Error handling is done in ViewModel.
     *
     * @param email email of user account
     * @param pass password of user account
     *
     * @return Firebase User object wrapped in Resource.Success class
     */
    suspend fun loginPerson(email: String, pass: String): Resource<FirebaseUser> {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()

        return Resource.Success(authResult.user!!)
    }

    /**
     * Check is user logged in to firebase auth
     *
     * @return boolean is there a logged in user
     */
    fun isUserLoggedIn(): Boolean{
        return firebaseAuth.currentUser != null
    }
}