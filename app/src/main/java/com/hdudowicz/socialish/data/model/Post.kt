package com.hdudowicz.socialish.data.model

import android.icu.text.RelativeDateTimeFormatter
import android.net.Uri
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import com.squareup.moshi.JsonClass
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
