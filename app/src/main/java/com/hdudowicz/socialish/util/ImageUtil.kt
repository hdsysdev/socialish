package com.hdudowicz.socialish.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.data.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.NullPointerException

object ImageUtil {

    fun savePostImage(context: Context, post: Post){
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(post.imageUri!!)
                .error(R.color.black)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .submit()
                .get()

            saveImageLocally(context, bitmap, post.postId)
        }
    }

    fun saveImageLocally(context: Context, image: Bitmap, name: String): String? {
        var savedImagePath: String? = null
        val fileName = "$name.jpg"
        val directoryPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            .toString() + "/saved_images"

        val directory = File(directoryPath)

        var directoryExists = true

        if (!directory.exists()) {
            directoryExists = directory.mkdirs()
        }

        if (directoryExists) {
            val imageFile = File(directory, fileName)
            savedImagePath = imageFile.absolutePath
            try {
                val outputFileStream: OutputStream = FileOutputStream(imageFile)

                image.compress(Bitmap.CompressFormat.JPEG, 100, outputFileStream)

                outputFileStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return savedImagePath
    }

    fun getImageFile(context: Context, name: String): File? {
        return try{
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + "/saved_images", "$name.jpg")
        } catch (e: NullPointerException){
            null
        }
    }


    fun deleteFile(context: Context, name: String): Boolean?{
        return getImageFile(context, name)?.delete()
    }
}