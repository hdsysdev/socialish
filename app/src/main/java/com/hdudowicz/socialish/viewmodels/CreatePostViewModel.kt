package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * ViewModel class for CreatePostActivity. Holds the entered post title, body, imageURI and all
 * business logic used by the activity.
 *
 */
class CreatePostViewModel : ViewModel() {
    // Email and pass variables updated using 2 way data binding in layout
    val titleText = ObservableField<String>("")
    val bodyText = ObservableField<String>("")
    var isAnonPost = ObservableField<Boolean>(true)

    // Initialise image URI for post as empty Uri
    var imageUri: MutableLiveData<Uri?> = MutableLiveData(null)

    // Post repository instance to access firebase to create posts
    private val postRepository = PostRepository()

    // LiveData boolean to show and hide a progress bar
    private val mCreatingPost = MutableLiveData<Boolean>(false)
    val isCreatingPost: LiveData<Boolean> = mCreatingPost

    // Checks if the title field is blank. Posts must have a
    fun isTitleBlank() = titleText.get().isNullOrBlank()

    /**
     * Creates a post from the entered post details. If an image was selected then an image post is created,
     * otherwise a normal post is made
     *
     * @return LiveData boolean indicating if creating the post was successful.
     */
    fun createPost(): LiveData<Boolean>{
        return if (imageUri.value == null){
            createNonImagePost()
        } else {
            createImagePostNew(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!, imageUri.value!!)
        }
    }

    /**
     * Create non image post on firebase with the entered title and body.
     *
     * @return LiveData boolean indicating if creating the normal post was successful
     */
    private fun createNonImagePost(): LiveData<Boolean> {
        val postCreated = MutableLiveData<Boolean>()
        mCreatingPost.postValue(true)
        // Using coroutine for network operations
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postRepository.createPost(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!)
                // Stop showing progress bar after creating a post
                mCreatingPost.postValue(false)
                postCreated.postValue(true)

            } catch (exception: Exception){
                Log.e("CREATE_POST", "Error creating post ", exception)
                postCreated.postValue(false)
                FirebaseCrashlytics.getInstance().recordException(exception)
            }
        }

        return postCreated
    }

    /**
     * Creates a new image post, first uploading the image to cloud storage then filling the post
     * document on Firestore.
     *
     * @param title of the new post
     * @param body of the new post
     * @param isAnon should the new post be anonymous
     * @param imgUri URI of the selected image to upload with the post
     * @return LiveData boolean indicating if the post was successfully created
     */
    private fun createImagePostNew(title: String, body: String, isAnon:Boolean, imgUri: Uri): LiveData<Boolean>{
        val postCreated = MutableLiveData<Boolean>()

        // Create post using coroutine then post success status boolean to returned live data
        // Using IO dispatcher since it's a network call
        viewModelScope.launch(Dispatchers.IO) {
            val createSuccess = postRepository.createImagePostNew(title, body, isAnon, imgUri)
            postCreated.postValue(createSuccess)
        }

        return postCreated
    }
}