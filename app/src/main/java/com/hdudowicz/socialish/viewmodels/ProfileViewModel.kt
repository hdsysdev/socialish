package com.hdudowicz.socialish.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel class for the ProfileFragment. Contains LiveData objects storing the displayed list of posts
 * in addition to functions for loading locally stored posts and the user's own posts depending on the
 * selected tab.
 *
 * @constructor creates new ProfileViewModel
 *
 * @param application reference to the current application to get context
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    // Post repository for loading posts from firebase
    private val postRepository = PostRepository()

    // List of displayed posts as LiveData to automatically update the UI when tabs are switched
    private val mDisplayedPostList: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postList: LiveData<ArrayList<Post>> = mDisplayedPostList

    // LiveData showing the name of the currently logged in user
    private val mDisplayName: MutableLiveData<String> = MutableLiveData("No Name")
    val displayName: LiveData<String> = mDisplayName

    // Index of the currently selected tab
    var selectedTab = 0

    /**
     * Loads the logged in user's own posts and dispatches them to the UI through the post list LiveData.
     *
     * @return LiveData returning a boolean of the success status of loading the user's posts
     */
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

    /**
     * Loads saved posts from local storage. Uses the application reference from constructor to get the context.
     */
    fun loadLocalPosts(){
        viewModelScope.launch(Dispatchers.IO){
            val posts = postRepository.getLocalPosts(getApplication<Application>().applicationContext)

            mDisplayedPostList.postValue(posts)
        }
    }

    /**
     * Load the display name of the current user
     */
    fun loadDisplayName(){
        mDisplayName.postValue(postRepository.getDisplayName())
    }
}