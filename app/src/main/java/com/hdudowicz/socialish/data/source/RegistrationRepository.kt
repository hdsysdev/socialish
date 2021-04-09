package com.hdudowicz.socialish.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdudowicz.socialish.data.model.Resource
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class RegistrationRepository {
    private var firebaseAuth: FirebaseAuth = Firebase.auth


    suspend fun registerNewUser(email: String, pass: String): Resource<FirebaseUser?> {
        return try {
            val registerStatus = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Resource.Success(registerStatus.user!!)
        } catch (exception: Exception){
            Resource.Error(exception)
        }
    }
}