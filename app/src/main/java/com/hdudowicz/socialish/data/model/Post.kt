package com.hdudowicz.socialish.data.model

import android.net.Uri
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

data class Post(
    val postId: String,
    val userId: String,
    val isImagePost: Boolean = false,
    val imageUri: Uri?,
    val title: String,
    val body: String,
    @field:JvmField
    val isAnonymous: Boolean = true,
    val datePosted: Date
)
data class CreatedPost(
    val userId: String?,
    @field:JvmField
    val isImagePost: Boolean? = false,
    val title: String?,
    val body: String?,
    @field:JvmField
    val isAnonymous: Boolean? = true,
    val datePosted: Date = Calendar.getInstance().time
)

