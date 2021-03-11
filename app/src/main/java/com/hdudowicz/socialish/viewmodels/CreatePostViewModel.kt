package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.hdudowicz.socialish.data.source.PostRepository
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

    fun isTitleBlank() = titleText.get().isNullOrBlank()

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


}