package com.hdudowicz.socialish.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel class for the PostFeedFragment. Stores the displayed list of posts and allows for loading
 * new posts
 *
 * @constructor Create new post feed ViewModel
 */
class PostFeedViewModel : ViewModel() {
    // Post repository instance to load posts from firebase
    private val postRepository = PostRepository()

    // LiveData storing an ArrayList of the latest posts. Used to notify the UI of updates in the post list
    private val mPostListLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postListLiveData: LiveData<ArrayList<Post>> = mPostListLiveData

    /**
     * Loads the newest Posts from all users and dispatches the loaded list to the UI through
     * mPostListLiveData.
     *
     * @return boolean success status of the network operation
     */
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