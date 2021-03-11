package com.hdudowicz.socialish.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository

class PostFeedViewModel : ViewModel() {
    private val postRepository = PostRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val mPostFeedLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postFeedLiveData: LiveData<ArrayList<Post>> = mPostFeedLiveData


    fun loadNewPosts() {
        postRepository.getPosts()
            .addOnSuccessListener { query ->
                val postList = arrayListOf<Post>()
                query.documents.forEach { doc ->
                    postList.add(
                        Post(
                            postId = doc.id,
                            userId = doc.getString("userId")!!,
                            isImagePost = doc.getBoolean("isImagePost")!!,
                            title = doc.getString("title")!!,
                            body = doc.getString("body")!!,
                            isAnonymous = doc.getBoolean("isAnonymous")!!,
                            datePosted = doc.getDate("datePosted")!!
                        )
                    )
                }
                mPostFeedLiveData.postValue(postList)
            }
    }

    fun uploadImage(uri: Uri) {

    }

}