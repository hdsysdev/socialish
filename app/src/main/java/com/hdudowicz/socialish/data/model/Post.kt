package com.hdudowicz.socialish.data.model

import android.net.Uri
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

/**
 * Data class for handling posts from firebase
 *
 * @property postId post object ID
 * @property userId ID of user who created the post
 * @property isImagePost is this  an image containing post
 * @property imageUri optional URI of the post image
 * @property title post title
 * @property body post body
 * @property isAnonymous should the post be anonymous
 * @property datePosted date the post was created
 *
 * @constructor Create new Post
 */
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

/**
 * Data class for submitting new posts to Firebase
 *
 * @property userId Auth user ID
 * @property isImagePost is this an image containing post
 * @property title post title
 * @property body post body
 * @property isAnonymous should post be anonymous
 * @property datePosted date when the post was made
 *
 * @constructor Create new CreatedPost
 */
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

