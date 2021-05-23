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

object DialogUtil {
    private val postRepository = PostRepository()

    fun showLogoutDialog(activity: Activity){
        AlertDialog.Builder(activity)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // Setting "yes" button to log user out of the app
            .setPositiveButton(
                activity.getString(R.string.yes)
            ) { dialog, which ->
                postRepository.logoutUser()
                activity.finish()
            }
            .setIcon(android.R.drawable.ic_menu_revert)
            // The null listener makes the button to dismiss the dialog with no further action
            .setNegativeButton(activity.getString(R.string.no), null)
            .show()
    }

    fun showDeleteDialog(context: Context, post: Post): LiveData<Boolean>{
        val wasSuccessful = MutableLiveData<Boolean>()
        AlertDialog.Builder(context)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete the post titled \"${post.title}\"?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // Setting "yes" button to log user out of the app
            .setPositiveButton(
                context.getString(R.string.yes)
            ) { dialog, which ->
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        postRepository.deletePost(post.postId)
                        wasSuccessful.postValue(true)
                    } catch (exception: Exception){
                        Toasty.error(context, "Failed to logout.").show()
                        wasSuccessful.postValue(false)
                    }
                }
            }
            .setIcon(android.R.drawable.ic_menu_revert)
            // The null listener makes the button to dismiss the dialog with no further action
            .setNegativeButton(context.getString(R.string.no), null)
            .show()

        return wasSuccessful
    }
}