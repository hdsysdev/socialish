package com.hdudowicz.socialish.viewmodels

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

    fun createPost(): LiveData<Resource<Boolean>> {
        val postCreated = MutableLiveData<Resource<Boolean>>()
        mCreatingPost.postValue(true)
        postRepository.createPost(titleText.get()!!, bodyText.get()!!, isAnonPost.get()!!)
            .addOnCompleteListener {
                // Show progress bar when creating a post
                mCreatingPost.postValue(false)

                if (it.isSuccessful){
                    postCreated.postValue(Resource.Success(true))
                } else {
                    Log.e("CREATE_POST", "Error creating post ", it.exception)
                    postCreated.postValue(Resource.Error(it.exception!!))
                }
            }
        return postCreated
    }
}