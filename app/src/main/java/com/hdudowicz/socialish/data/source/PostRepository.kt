package com.hdudowicz.socialish.data.source

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.hdudowicz.socialish.data.model.CreatedPost
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.util.ImageUtil
import com.hdudowicz.socialish.util.PostConverter
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * Repository for the MainActivity contains functions related to querying, creating and deleting posts on
 * Firebase.
 *
 * @constructor Create post repository
 */
class PostRepository
{
    // Firebase Firestore, Auth and Cloud Storage instances allowing access to firebase features
    private val firestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth
    private val imgageStoreRef = Firebase.storage.reference

    /**
     * Get display name of the currently logged in user
     *
     * @return returns the name of the logged in user or null
     */
    fun getDisplayName(): String? {
        var name = firebaseAuth.currentUser?.displayName
        if (name == "" || name == null){
            name = firebaseAuth.currentUser?.email
        }
        return name
    }

    /**
     * Logout user from Firebase Auth
     */
    fun logoutUser(){
        firebaseAuth.signOut()
    }

    /**
     * Create a new post
     *
     * @param title
     * @param body
     * @param isAnon
     * @return
     */
    suspend fun createPost(title: String, body: String, isAnon: Boolean): DocumentReference {
        val post = CreatedPost(
            userId = firebaseAuth.currentUser?.uid,
            title = title,
            body = body,
            isAnonymous = isAnon
        )
        return firestore.collection("posts").add(post).await()
    }


    /**
     * Create image post new
     *
     * @param title
     * @param body
     * @param isAnon
     * @param imageUri
     * @return
     */
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

    /**
     * Get current user posts
     *
     * @return
     */
    suspend fun getCurrentUserPosts(): ArrayList<Post>? {
        return try {
            val query = firestore.collection("posts")
                .orderBy("datePosted", Query.Direction.DESCENDING)
                .whereEqualTo("userId", firebaseAuth.currentUser?.uid)
                .get()
                .await()

            adaptDocsToPosts(query.documents)
        } catch (e: Exception){
            Log.e("POST", "Error getting current user posts", e)
            null
        }

    }

    /**
     * Get posts
     *
     * @return
     */
    suspend fun getPosts(): ArrayList<Post>? {
        // TODO: Limit loaded posts
        return try {
            val query = firestore.collection("posts")
                .orderBy("datePosted", Query.Direction.DESCENDING)
                .get()
                .await()

            adaptDocsToPosts(query.documents)
        } catch (e: Exception){
            null
        }
    }

    /**
     * Get local posts
     *
     * @param context
     * @return
     */
    fun getLocalPosts(context: Context): ArrayList<Post>{
        val prefs = context.getSharedPreferences("saved_posts", Context.MODE_PRIVATE)
        val postList = arrayListOf<Post>()

        prefs.getStringSet("saved_posts", setOf())?.forEach { postString ->
            val post = PostConverter.postFromJson(postString)
            if(post != null){
                postList.add(post)
            }
        }

        return ArrayList(postList.asReversed())
    }

    /**
     * Delete local post
     *
     * @param context
     * @param post
     */
    fun deleteLocalPost(context: Context, post: Post): Boolean {
        val savedPosts = getLocalPosts(context)

        if (post.imageUri != null){
            ImageUtil.deleteFile(context, post.postId)
        }

        savedPosts.removeIf { post.postId == it.postId }

        return updateLocalPosts(context, savedPosts)
    }

    /**
     * Checks if passed post has is in local storage
     *
     * @param context used to access shared preferences
     * @param post to find locally
     * @return if post is in local storage
     */
    fun isPostSaved(context: Context, post: Post): Boolean {
        val localPosts = getLocalPosts(context)

        return localPosts.find { post.postId == it.postId } != null
    }

    /**
     * Update local posts
     *
     * @param context
     * @param posts
     */
    fun updateLocalPosts(context: Context, posts: ArrayList<Post>): Boolean {
        val prefs = context.getSharedPreferences("saved_posts", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val postSet = hashSetOf<String>()
        posts.forEach { post ->
            postSet.add(PostConverter.postToJson(post))
        }

        editor.putStringSet("saved_posts", postSet)

        // Using commit() not apply() to instantly get if updating the local storage was successful
        return editor.commit()
    }


    /**
     * Save post locally
     *
     * @param context
     * @param post
     */
    fun savePostLocally(context: Context, post: Post): Boolean {
        val prefs = context.getSharedPreferences("saved_posts", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        if (post.imageUri != null){
            ImageUtil.savePostImage(context, post)
        }


        val savedPosts = HashSet(prefs.getStringSet("saved_posts", setOf()))

        val postJson = PostConverter.postToJson(post)
        savedPosts.add(postJson)

        editor.putStringSet("saved_posts", savedPosts)

        // Using commit() not apply() to instantly get if updating the local storage was successful
        return editor.commit()
    }

    /**
     * Suspending function to adapt firebase documents to an array list of post objects
     *
     * @param docList list of firebase documents to convert to post objects
     */
    private suspend fun adaptDocsToPosts(docList: List<DocumentSnapshot>): ArrayList<Post>{
        val postList = arrayListOf<Post>()
        docList.forEach { doc ->
            val isImagePost = doc.getBoolean("isImagePost")!!
            var imgUri: Uri? = null
            if (isImagePost) {
                imgUri = try {
                    imgageStoreRef.child(doc.id).downloadUrl.await()
                } catch (e: Exception) {
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
    }

    /**
     * Upload post image
     *
     * @param uri
     * @param postDocId
     * @return
     */
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