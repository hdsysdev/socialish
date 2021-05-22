package com.hdudowicz.socialish.adapters

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Moshi adapter for converting Uri objects to and from JSON
 *
 */
class UriAdapter {
    @FromJson
    fun fromJson(json: String): Uri {
        return Uri.parse(json)
    }

    @ToJson
    fun toJson(uri: Uri): String? {
        return uri.path
    }
}