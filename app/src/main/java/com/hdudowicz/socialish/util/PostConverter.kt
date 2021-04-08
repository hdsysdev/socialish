package com.hdudowicz.socialish.util

import android.net.Uri
import com.hdudowicz.socialish.adapters.UriAdapter
import com.hdudowicz.socialish.data.model.Post
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

object PostConverter{
    val moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .add(UriAdapter())
        .build()
    val adapter: JsonAdapter<Post> = moshi.adapter(Post::class.java)


    fun postToJson(post: Post): String{
        return adapter.toJson(post)
    }

    fun postFromJson(post: String): Post? {
        return adapter.fromJson(post)
    }
}