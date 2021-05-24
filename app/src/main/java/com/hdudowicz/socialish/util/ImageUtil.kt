package com.hdudowicz.socialish.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.data.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Static class containing utility functions for handling images within the application.
 *
 */
object ImageUtil {
    /**
     * Gets a Bitmap object from the image URI of the passed Post using Glide. The bitmap is saved
     * locally on the device's storage.
     *
     * @param context of the activity/fragment is being launched in
     * @param post object to download image from
     */
    fun savePostImage(context: Context, post: Post){
        // Using coroutine for image downloading and saving image to local storage
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

    /**
     * Saves the passed Bitmap object to a JPEG file stored in a folder called "saved_images" in the
     * user's picture directory.
     *
     * @param context from the activity/fragment image is being saved from
     * @param image Bitmap object to be saved locally
     * @param name to give the image file
     * @return path to the saved image if successful or null if not
     */
    private fun saveImageLocally(context: Context, image: Bitmap, name: String): String? {
        var savedImagePath: String? = null
        val fileName = "$name.jpg"
        // Using the standard Android picture directory
        val directoryPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            .toString() + "/saved_images"

        val directory = File(directoryPath)

        var directoryExists = true

        // If the directory doesn't exist then create it
        if (!directory.exists()) {
            directoryExists = directory.mkdirs()
        }

        // If the directory exists then create the image file and write an output stream of the JPEG to it.
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

    /**
     * Get File object for the locally stored image file with the same name passed.
     *
     * @param context from the activity/fragment the function is running in
     * @param name of the file to get
     * @return File object for the found image or null if image doesn't exist
     */
    fun getImageFile(context: Context, name: String): File? {
        return try{
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + "/saved_images", "$name.jpg"
            )
        } catch (e: NullPointerException){
            null
        }
    }

    /**
     * Gets a locally stored file with the passed name and deletes it
     *
     * @param context from the activity/fragment running the function
     * @param name of the file to delete
     * @return boolean if deleting the file was successful
     */
    fun deleteFile(context: Context, name: String): Boolean?{
        return getImageFile(context, name)?.delete()
    }
}