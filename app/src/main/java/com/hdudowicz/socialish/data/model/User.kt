package com.hdudowicz.socialish.data.model

import java.io.Serializable


class User(var uid: String, var name: String, var email: String) : Serializable {
    var isAuthenticated = false
    var isNew = false



}