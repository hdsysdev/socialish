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

class ProfileViewModel : ViewModel() {
    private val postRepository = PostRepository()


    private val mDisplayedPostListLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postList: LiveData<ArrayList<Post>> = mDisplayedPostListLiveData

    fun loadMyPosts(): LiveData<Boolean> {
        val loadSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getCurrentUserPosts()

            if (posts != null){
                mDisplayedPostListLiveData.postValue(posts)
                loadSuccess.postValue(true)
            } else {
                loadSuccess.postValue(false)
            }
        }
        return loadSuccess
    }

    fun getProfilePicUri(): Uri?{
        return postRepository.getProfilePicUrl()
    }

}