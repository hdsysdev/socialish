package com.hdudowicz.socialish.data.model

import android.icu.text.RelativeDateTimeFormatter
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Post(
    val userId: String? = null,
    val imageId: String? = null,
    val title: String? = null,
    val body: String? = null,
    @field:JvmField
    val isAnonymous: Boolean? = true,
    val datePosted: Date? = null
)
data class CreatedPost(
    val userId: String?,
    val imageId: String?,
    val title: String?,
    val body: String?,
    @field:JvmField
    val isAnonymous: Boolean? = true,
    val datePosted: Date = Calendar.getInstance().time
)
