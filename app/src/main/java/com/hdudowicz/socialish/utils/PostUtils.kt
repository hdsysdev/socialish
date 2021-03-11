package com.hdudowicz.socialish.utils

import android.content.Context
import android.net.Uri
import androidx.databinding.ViewDataBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
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

    fun imageUrlById(id: String): Task<Uri> {
        return Firebase.storage.reference.child(id).downloadUrl
    }

    // Extension function getting context from DataBinding classes
    fun ViewDataBinding.getContext(): Context{
        return root.context
    }

}