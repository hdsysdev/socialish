package com.hdudowicz.socialish.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val postRepository = PostRepository()


    private val mDisplayedPostList: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postList: LiveData<ArrayList<Post>> = mDisplayedPostList

    private val mDisplayName: MutableLiveData<String> = MutableLiveData("No Name")
    val displayName: LiveData<String> = mDisplayName

    var selectedTab = 0

    fun loadMyPosts(): LiveData<Boolean> {
        val loadSuccess = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getCurrentUserPosts()

            if (posts != null){
                mDisplayedPostList.postValue(posts)
                loadSuccess.postValue(true)
            } else {
                loadSuccess.postValue(false)
            }
        }
        return loadSuccess
    }

    fun loadLocalPosts(){
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getLocalPosts(getApplication<Application>().applicationContext)

            mDisplayedPostList.postValue(posts)
        }
    }

    fun loadDisplayName(){
        mDisplayName.postValue(postRepository.getDisplayName())
    }


    fun logoutUser(){
        postRepository.logoutUser()
    }
}