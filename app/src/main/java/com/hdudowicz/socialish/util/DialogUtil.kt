package com.hdudowicz.socialish.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * Static utility class containing functions to create and show AlertDialogs for different actions.
 *
 */
object DialogUtil {
    // Creating post repository variable to access firebase
    private val postRepository = PostRepository()

    /**
     * Shows an AlertDialog which, upon confirmation, logs the user out of the app and closes the
     * passed Activity.
     *
     * @param activity the activity the AlertDialog is launched in
     */
    fun showLogoutDialog(activity: Activity){
        AlertDialog.Builder(activity)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            // Specifying a listener to take an action before dismissing the dialog.
            // Setting "yes" button to log user out of the app
            .setPositiveButton(
                activity.getString(R.string.yes)
            ) { _, _ ->
                postRepository.logoutUser()
                activity.finish()
            }
            .setIcon(android.R.drawable.ic_menu_revert)
            // A null listener makes the button dismiss the dialog
            .setNegativeButton(activity.getString(R.string.no), null)
            .show()
    }

    /**
     * Shows an AlertDialog which upon confirming deletes the passed Post from Firebase and posts a
     * success status boolean to the returned LiveData. Displays an error message upon failure.
     *
     * @param context the dialog is launched in
     * @param post object to delete from firebase
     * @return LiveData returning a success status boolean after the network call to firebase
     */
    fun showDeleteDialog(context: Context, post: Post): LiveData<Boolean>{
        val wasSuccessful = MutableLiveData<Boolean>()
        AlertDialog.Builder(context)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete the post titled \"${post.title}\"?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // Setting "yes" button to delete the post
            .setPositiveButton(
                context.getString(R.string.yes)
            ) { _, _ ->
                // Using coroutine for network operations.
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        postRepository.deletePost(post.postId)
                        wasSuccessful.postValue(true)
                    } catch (exception: Exception){
                        Toasty.error(context, "Failed to delete post.").show()
                        wasSuccessful.postValue(false)
                    }
                }
            }
            .setIcon(android.R.drawable.ic_menu_revert)
            .setNegativeButton(context.getString(R.string.no), null)
            .show()

        return wasSuccessful
    }
}