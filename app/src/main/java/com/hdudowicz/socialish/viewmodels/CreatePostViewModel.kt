package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.UploadTask
import com.hdudowicz.socialish.data.model.CreatedPost
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.data.source.LoginRepository
import com.hdudowicz.socialish.data.source.PostRepository
import com.hdudowicz.socialish.utils.PostUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    // Email and pass variables updated using 2 way binding in layout
    val titleText = ObservableField<String>("")
    val bodyText = ObservableField<String>("")
    var isAnonPost = ObservableField<Boolean>(true)

    // Initialise image URI for post as empty Uri
    var imageUri: MutableLiveData<Uri?> = MutableLiveData(null)

    val postRepository = PostRepository()

    private val mCreatingPost = MutableLiveData<Boolean>(false)
    val creatingPost: LiveData<Boolean> = mCreatingPost

//    fun getCurrentPost(): CreatedPost {
//        return CreatedPost(
//            title = titleText.get(),
//            body = bodyText.get(),
//            isAnonymous = isAnonPost.get()
//        )
//    }

    fun createPost(): LiveData<Boolean>{
        return if (imageUri.value == null){
            createNonImagePost()
        } else {
            createImagePostNew(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!, imageUri.value!!)
        }
    }

    fun createNonImagePost(): LiveData<Boolean> {
        val postCreated = MutableLiveData<Boolean>()
        mCreatingPost.postValue(true)
        postRepository.createPost(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!)
            .addOnCompleteListener {
                // Show progress bar when creating a post
                mCreatingPost.postValue(false)

                if (it.isSuccessful) {
                    postCreated.postValue(true)
                } else {
                    Log.e("CREATE_POST", "Error creating post ", it.exception)
                    postCreated.postValue(false)
                }
            }


        return postCreated
    }

    fun createImagePostNew(title: String, body: String, isAnon:Boolean, imgUri: Uri): LiveData<Boolean>{
        val postCreated = MutableLiveData<Boolean>()

        // Create post using coroutine then post success status boolean to returned live data
        // Using IO dispatcher since it's a network call
        viewModelScope.launch(Dispatchers.IO) {
            val createSuccess = postRepository.createImagePostNew(title, body, isAnon, imgUri)
            postCreated.postValue(createSuccess)
        }

        return postCreated
    }

    //TODO: Convert to subroutine
//    fun createImagePost(imgUri: Uri): LiveData<Resource<Boolean>> {
//        // LiveData for returning result of creating image post
//        val postCreated = MutableLiveData<Resource<Boolean>>()
//        // Setting creating post in progress to show progress bar
//        mCreatingPost.postValue(true)
//
//        postRepository.createImagePost(
//            titleText.get()!!,
//            bodyText.get()!!,
//            isAnonPost.get()!!
//        ).addOnCompleteListener { task ->
//                // Show progress bar when creating a post
//                mCreatingPost.postValue(false)
//                if (task.isSuccessful) {
//                    // Upload post image if creating document is successful with the name being the document id
//                    createPostImage(imgUri, task.result!!.id)
//                        .addOnCompleteListener {
//                            postCreated.postValue(
//                                if (it.isSuccessful)
//                                    Resource.Success(true)
//                                else {
//                                    Log.e("CREATE_POST", "Error uploading post image ", task.exception)
//                                    Resource.Error(it.exception!!)
//                                }
//                            )
//                        }
//                } else {
//                    Log.e("CREATE_POST", "Error creating post ", task.exception)
//                    postCreated.postValue(Resource.Error(task.exception!!))
//                }
//            }
//        return postCreated
//    }
//
//    fun createPostImage(uri: Uri, postId: String): UploadTask {
//        return postRepository.uploadPostImage(uri, postId)
//    }
}