package com.hdudowicz.socialish.util

import com.hdudowicz.socialish.adapters.UriAdapter
import com.hdudowicz.socialish.data.model.Post
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

/**
 * Static utility class for serialising and deserialising Post objects to and from JSON using the Moshi
 * library.
 *
 */
object PostConverter{
    // Building a Moshi instance with a Date adapter for serialising dates and the custom URI adapter for serializing URIs
    private val moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .add(UriAdapter())
        .build()
    private val adapter: JsonAdapter<Post> = moshi.adapter(Post::class.java)

    /**
     * Serialise a Post object into a JSON string
     *
     * @param post object to serialize into JSON
     * @return serialised Post object JSON string
     */
    fun postToJson(post: Post): String{
        return adapter.toJson(post)
    }

    /**
     * Deserialize a JSON string into a Post object
     *
     * @param post JSON Post object string
     * @return deserialised Post object or null if failed
     */
    fun postFromJson(post: String): Post? {
        return adapter.fromJson(post)
    }
}