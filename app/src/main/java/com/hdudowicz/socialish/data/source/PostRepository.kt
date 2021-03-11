package com.hdudowicz.socialish.data.source

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.hdudowicz.socialish.data.model.CreatedPost
import com.hdudowicz.socialish.data.model.Post
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class PostRepository
{
    private val firestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth
    private val imgageStoreRef = Firebase.storage.reference


    fun createPost(title: String, body: String, isAnon: Boolean): Task<DocumentReference> {
        val post = CreatedPost(
            userId = firebaseAuth.currentUser?.uid,
            title = title,
            body = body,
            isAnonymous = isAnon
        )
        return firestore.collection("posts").add(post)
    }

    fun createImagePost(title: String, body: String, isAnon: Boolean): Task<DocumentReference> {
        val post = CreatedPost(
            userId = firebaseAuth.currentUser?.uid,
            title = title,
            body = body,
            isAnonymous = isAnon,
            isImagePost = true
        )
        return firestore.collection("posts").add(post)
    }

    suspend fun createImagePostNew(title: String, body: String, isAnon: Boolean, imageUri: Uri): Boolean{
        val docRef = firestore.collection("posts").document()

        val imageUpload = uploadPostImage(imageUri, docRef.id)

        // If image upload was successful then create post document
        if (imageUpload){
            return try {
                docRef.set(CreatedPost(
                    userId = firebaseAuth.currentUser?.uid,
                    title = title,
                    body = body,
                    isAnonymous = isAnon,
                    isImagePost = true
                ))
                true
            } catch (e: Exception){
                Log.e("POST", "Failed to create post document", e)
                false
            }
        } else {
            return false
        }
    }

    fun getCurrentUserPosts(): Task<QuerySnapshot> {
        return firestore.collection("posts")
            .whereEqualTo("userId", firebaseAuth.currentUser?.uid)
            .get()
    }

    suspend fun getPosts(): ArrayList<Post>? {
        try {
            val postList = arrayListOf<Post>()
            val query = firestore.collection("posts")
                .orderBy("datePosted", Query.Direction.DESCENDING)
                .limit(25)
                .get()
                .await()

            query.documents.forEach { doc ->
                val isImagePost = doc.getBoolean("isImagePost")!!
                var imgUri: Uri? = null
                if (isImagePost){
                    imgUri = try {
                        imgageStoreRef.child(doc.id).downloadUrl.await()
                    } catch (e: Exception){
                        null
                    }
                }

                postList.add(
                    Post(
                        postId = doc.id,
                        userId = doc.getString("userId")!!,
                        isImagePost = isImagePost,
                        imageUri = imgUri,
                        title = doc.getString("title")!!,
                        body = doc.getString("body")!!,
                        isAnonymous = doc.getBoolean("isAnonymous")!!,
                        datePosted = doc.getDate("datePosted")!!
                    )
                )
            }
            return postList
        } catch (e: Exception){
            return null
        }

    }

    fun getPostsAfter(index: Int): Task<QuerySnapshot> {
        return firestore.collection("posts")
            .orderBy("datePosted")
            .startAfter(index)
            .limit(25)
            .get()
    }

    fun uploadProfileImage(uri: Uri): UploadTask {
        val profImgRef = imgageStoreRef.child(firebaseAuth.uid!!)
        return profImgRef.putFile(uri)
    }

    suspend fun uploadPostImage(uri: Uri, postDocId: String): Boolean {
        val postImgRef = imgageStoreRef.child(postDocId)
        return try {
            postImgRef.putFile(uri).await()
            true
        } catch (exception: Exception){
            false
        }
    }
}