package com.hdudowicz.socialish.viewmodels

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


    fun loadNewPosts(){
        postRepository.getPosts()
            .addOnSuccessListener { query ->
                val postList = arrayListOf<Post>()
                query.documents.forEach { doc ->
                    val post = doc.toObject(Post::class.java)
                    if (post != null){
                        postList.add(post)
                    }

                }
                mPostFeedLiveData.postValue(postList)
            }
    }

}