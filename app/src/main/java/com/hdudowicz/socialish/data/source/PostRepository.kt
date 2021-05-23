package com.hdudowicz.socialish.data.source

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hdudowicz.socialish.data.model.CreatingPost
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
     * Get display name of the currently logged in user from Firebase Auth
     *
     * @return returns the name of the logged in user or null if no user logged in
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
     * Suspending function to create a new post on Firebase
     *
     * @param title title of the post
     * @param body post body
     * @param isAnon should the post be anonymous
     * @return DocumentReference to the newly created post on Firestore
     */
    suspend fun createPost(title: String, body: String, isAnon: Boolean): DocumentReference {
        // CreatingPost object containing all the required fields
        val post = CreatingPost(
            userId = firebaseAuth.currentUser?.uid,
            title = title,
            body = body,
            isAnonymous = isAnon
        )
        return firestore.collection("posts").add(post).await()
    }


    suspend fun deletePost(postDocId: String){
        firestore.collection("posts").document(postDocId).delete().await()
    }

    /**
     * Suspending function to create a new image post. Creates a new Firestore document, uploads passed image
     * to Firebase Cloud Storage with the name as the Firestore document's ID then updates post document with
     * new post data.
     *
     * @param title for the image post
     * @param body of the image post
     * @param isAnon should the post be anonymous
     * @param imageUri URI of the post image
     * @return if creating the image post was successful
     */
    suspend fun createImagePostNew(title: String, body: String, isAnon: Boolean, imageUri: Uri): Boolean{
        // Get reference to new document in "posts" firestore collection
        val docRef = firestore.collection("posts").document()

        // Upload post image from the passed URI with the title as the new post document reference ID
        val imageUpload = uploadPostImage(imageUri, docRef.id)

        // If image upload was successful then create post document
        if (imageUpload){
            return try {
                docRef.set(CreatingPost(
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
     * Suspending function to get a list of currently logged in user's posts
     *
     * @return ArrayList of logged in users posts or null
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
     * Suspending function to get posts the latest posts.
     *
     * @return ArrayList of latest posts or null if exception was thrown
     */
    suspend fun getPosts(): ArrayList<Post>? {
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
     * Get string set of locally stored post JSON from the app's SharedPreferences, deserializes them into Post objects
     * using Moshi and returns them.
     *
     * @param context the function was launched in, used for getting access to SharedPreferences
     * @return an ArrayList of locally stored Post objects
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
     * Deletes a locally stored post from SharedPreferences. If it's an image post, also removes the
     * post image from the device's storage.
     *
     * @param context Context the function was launched in to get access to SharedPreferences
     * @param post the post object to be removed from local storage
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
     * Checks if the passed post is saved in local storage
     *
     * @param context used to access shared preferences
     * @param post to find in local storage
     * @return boolean if post is in local storage
     */
    fun isPostSaved(context: Context, post: Post): Boolean {
        val localPosts = getLocalPosts(context)

        return localPosts.find { post.postId == it.postId } != null
    }

    /**
     * Updates the locally stored list of Posts in SharedPreferences
     *
     * @param context to access SharedPreferences
     * @param posts List of posts to replace the locally stored posts with
     */
    private fun updateLocalPosts(context: Context, posts: ArrayList<Post>): Boolean {
        val prefs = context.getSharedPreferences("saved_posts", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val postSet = hashSetOf<String>()
        posts.forEach { post ->
            postSet.add(PostConverter.postToJson(post))
        }

        editor.putStringSet("saved_posts", postSet)

        // Using commit() not apply() to synchronously commit to SharedPrefs and instantly get
        // the result of updating local storage.
        return editor.commit()
    }


    /**
     * Save the passed Post object locally to SharedPreferences and synchronously commit changes.
     * If it is an image post then save the image to local storage using ImageUtil.savePostImage.
     *
     * @param context used to access SharedPreferences
     * @param post object to store locally
     */
    fun savePostLocally(context: Context, post: Post): Boolean {
        val prefs = context.getSharedPreferences("saved_posts", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // If the post contains an image URI then save the image to local storage
        if (post.imageUri != null){
            ImageUtil.savePostImage(context, post)
        }

        // Getting set of locally stored, serialised Post JSON strong
        val savedPosts = HashSet(prefs.getStringSet("saved_posts", setOf()))

        // Serialising the Post object to JSON
        val postJson = PostConverter.postToJson(post)
        savedPosts.add(postJson)

        editor.putStringSet("saved_posts", savedPosts)

        // Using commit() not apply() to instantly get if updating the local storage was successful
        return editor.commit()
    }

    /**
     * Suspending function to adapt firebase documents to an array list of Post objects.
     *
     * @param docList list of Firestore documents to convert to Post objects
     * @return ArrayList of Post objects converted from Firestore documents
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
     * Suspending function to upload a post image at the passed URI to Firebase Cloud Storage.
     *
     * @param uri of the post image
     * @param postDocId ID of the corresponding Post Firestore document
     * @return boolean indicating if image uploading was successful
     */
    private suspend fun uploadPostImage(uri: Uri, postDocId: String): Boolean {
        val postImgRef = imgageStoreRef.child(postDocId)
        return try {
            postImgRef.putFile(uri).await()
            true
        } catch (exception: Exception){
            false
        }
    }
}