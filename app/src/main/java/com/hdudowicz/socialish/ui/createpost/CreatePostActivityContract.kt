package com.hdudowicz.socialish.ui.createpost

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.hdudowicz.socialish.data.model.Post

//class EditPostActivityContract : ActivityResultContract<String, Post?>() {
//
//    override fun createIntent(context: Context, inputPost: String): Intent {
//        return Intent(context, CreatePostActivity::class.java).apply {
//            if (inputPost.isNotEmpty()){
//                putExtra("post", inputPost)
//            }
//        }
//    }
//
//    override fun parseResult(resultCode: Int, intent: Intent?): Post? {
//        val data = intent?.getStringExtra("title")
//        return if (resultCode == Activity.RESULT_OK && data != null) data
//        else null
//    }
//}
//
//class CreatePostActivityContract : ActivityResultContract<String, Post?>() {
//
//    override fun createIntent(context: Context, inputPost: String): Intent {
//        return Intent(context, CreatePostActivity::class.java)
//    }
//
//    override fun parseResult(resultCode: Int, intent: Intent?): Post? {
//        val data = intent?.getStringExtra("post")
//        return if (resultCode == Activity.RESULT_OK && data != null) data
//        else null
//    }
//}