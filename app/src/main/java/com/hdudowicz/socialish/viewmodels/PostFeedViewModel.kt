package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostFeedViewModel : ViewModel() {
    private val postRepository = PostRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val mPostFeedLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postFeedLiveData: LiveData<ArrayList<Post>> = mPostFeedLiveData


    fun loadNewPosts(): LiveData<Boolean> {
        val loadSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getPosts()

            if (posts != null){
                mPostFeedLiveData.postValue(posts)
                loadSuccess.postValue(true)
            } else {
                loadSuccess.postValue(false)
            }


        }
        return loadSuccess
    }

    fun refreshPosts(){

    }

    fun uploadImage(uri: Uri) {

    }

}