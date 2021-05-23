package com.hdudowicz.socialish.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdudowicz.socialish.data.model.Resource
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * Repository class for RegistrationActivity. Contains a function for registering a new user with
 * Firebase Auth.
 *
 * @constructor Create new registration repository
 */
class RegistrationRepository {
    // Getting instance of Firebase Authentication
    private var firebaseAuth: FirebaseAuth = Firebase.auth

    /**
     * Register a new user on Firebase Auth with the passed email and password
     *
     * @param email to create the account with
     * @param pass the password to create the account with
     * @return Resource wrapper with a FirebaseUser object if the registration succeeded or an exception if failed
     */
    suspend fun registerNewUser(email: String, pass: String): Resource<FirebaseUser?> {
        return try {
            val registerStatus = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Resource.Success(registerStatus.user!!)
        } catch (exception: Exception){
            Resource.Error(exception)
        }
    }
}