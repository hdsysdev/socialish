package com.hdudowicz.socialish.viewmodels

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

    private val mPostListLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postListLiveData: LiveData<ArrayList<Post>> = mPostListLiveData


    fun loadNewPosts(): LiveData<Boolean> {
        val loadSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getPosts()

            if (posts != null){
                mPostListLiveData.postValue(posts)
                loadSuccess.postValue(true)
            } else {
                loadSuccess.postValue(false)
            }
        }
        return loadSuccess
    }


}