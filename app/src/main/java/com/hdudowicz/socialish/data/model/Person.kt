package com.hdudowicz.socialish.data.model

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable


class Person(var uid: String,
           var name: String,
           var email: String)  {
    var isAuthenticated = false
    var isNew = false


    companion object{
        fun adaptFirebaseUser(firebaseUser: FirebaseUser): Person {
            val user = Person(uid = firebaseUser.uid,
                name = firebaseUser.displayName?: "",
                email = firebaseUser.email?: "")
//            user.isAuthenticated = firebaseUser.
            return user
        }
    }

}