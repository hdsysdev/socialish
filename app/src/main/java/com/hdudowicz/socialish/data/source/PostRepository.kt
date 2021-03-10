package com.hdudowicz.socialish.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hdudowicz.socialish.data.model.CreatedPost
import com.hdudowicz.socialish.data.model.Post

class PostRepository
{
    private val firestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    fun createPost(title: String, body: String, isAnon: Boolean): Task<DocumentReference> {
        val post = CreatedPost(
            userId = firebaseAuth.currentUser?.uid,
            title = title,
            body = body,
            isAnonymous = isAnon,
            imageId = null
        )
        return firestore.collection("posts").add(post)
    }

    fun getCurrentUserPosts(): Task<QuerySnapshot> {
        return firestore.collection("posts")
            .whereEqualTo("userId", firebaseAuth.currentUser?.uid)
            .get()
    }

    fun getPosts(): Task<QuerySnapshot> {
        return firestore.collection("posts")
            .orderBy("datePosted", Query.Direction.DESCENDING)
            .limit(25)
            .get()
    }

    fun getPostsAfter(index: Int): Task<QuerySnapshot> {
        return firestore.collection("posts")
            .orderBy("datePosted")
            .startAfter(index)
            .limit(25)
            .get()
    }
}