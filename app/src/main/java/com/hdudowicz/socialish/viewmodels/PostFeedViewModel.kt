package com.hdudowicz.socialish.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hdudowicz.socialish.data.model.Post

class PostFeedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val mPostFeedLiveData: MutableLiveData<ArrayList<Post>> = MutableLiveData()
    val postFeedLiveData: LiveData<ArrayList<Post>> = mPostFeedLiveData


}