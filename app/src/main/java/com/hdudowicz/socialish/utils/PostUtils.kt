package com.hdudowicz.socialish.utils

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.hdudowicz.socialish.data.model.Post
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

// Static utility class of functions
object PostUtils {
    // Building moshi object with date adapter for parsing post objects to and from JSON
    private val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()
    private val postAdapter = moshi.adapter(Post::class.java)

    fun postToJson(post: Post): String{
        return postAdapter.toJson(post)
    }

    fun postFromJson(json: String): Post? {
        return postAdapter.fromJson(json)
    }


    // Extension function getting context from DataBinding classes
    fun ViewDataBinding.getContext(): Context{
        return root.context
    }

}