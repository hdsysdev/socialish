package com.hdudowicz.socialish.data.model

import android.icu.text.RelativeDateTimeFormatter
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import java.util.*

data class Post(
    val postId: String,
    val postedUserId: String,
    val imageId: String?,
    val title: String,
    val body: String,
    val anonymousPost: Boolean,
    val datePosted: Date
)
