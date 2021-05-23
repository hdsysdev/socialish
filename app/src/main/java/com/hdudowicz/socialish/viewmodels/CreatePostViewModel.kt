package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class CreatePostViewModel : ViewModel() {
    // Email and pass variables updated using 2 way binding in layout
    val titleText = ObservableField<String>("")
    val bodyText = ObservableField<String>("")
    var isAnonPost = ObservableField<Boolean>(true)

    // Initialise image URI for post as empty Uri
    var imageUri: MutableLiveData<Uri?> = MutableLiveData(null)

    val postRepository = PostRepository()

    private val mCreatingPost = MutableLiveData<Boolean>(false)
    val isCreatingPost: LiveData<Boolean> = mCreatingPost

    fun isTitleBlank() = titleText.get().isNullOrBlank()

    fun createPost(): LiveData<Boolean>{
        return if (imageUri.value == null){
            createNonImagePost()
        } else {
            createImagePostNew(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!, imageUri.value!!)
        }
    }

    private fun createNonImagePost(): LiveData<Boolean> {
        val postCreated = MutableLiveData<Boolean>()
        mCreatingPost.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postRepository.createPost(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!)

                // Stop showing progress bar after creating a post
                mCreatingPost.postValue(false)
                postCreated.postValue(true)

            } catch (exception: Exception){
                Log.e("CREATE_POST", "Error creating post ", exception)
                postCreated.postValue(false)
            }
        }

        return postCreated
    }

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