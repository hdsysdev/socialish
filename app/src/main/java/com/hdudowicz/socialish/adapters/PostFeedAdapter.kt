package com.hdudowicz.socialish.adapters

import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.PostListItemBinding
import com.hdudowicz.socialish.utils.PostUtils.getContext
import com.hdudowicz.socialish.utils.PostUtils.imageUrlById
import java.util.*

// Using list adapter instead of RecyclerView adapter because it automates change detection in data set using DiskUtil thus reducing boilerplate code.
class PostFeedAdapter(): ListAdapter<Post, PostFeedAdapter.PostViewHolder>(PostItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Create post view using post list item layout binding
        val bind = PostListItemBinding.inflate(layoutInflater, parent, false)

        return PostViewHolder(bind)
    }

    // Overriding onBindViewHolder to initialise Post object databinding
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.binding.post = post

        if (post.isImagePost){
            holder.binding.postImage.visibility = View.VISIBLE

            val progressDrawable = CircularProgressDrawable(holder.binding.getContext())
            progressDrawable.strokeWidth = 4f
            progressDrawable.centerRadius = 25f
            progressDrawable.start()

            imageUrlById(post.postId)
                .addOnSuccessListener {
                    // Load image using link to firebase storage with postId
                    Glide.with(holder.binding.getContext())
                        .load(it)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .fitCenter()
                        .placeholder(progressDrawable)
                        .into(holder.binding.postImage)

                }
                .addOnFailureListener {
                    Log.e("POST", "Failed to load post ", it)
                }
        }


        // Listener for share button using android Sharesheet to share post text to other apps
        holder.binding.sharePost.setOnClickListener {
            // Creating share text
            val shareText = "Socialish Post - \nTitle: ${post.title} \nBody: ${post.body}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, post.title)
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            it.context.startActivity(shareIntent)
        }


    }


    // Using a ViewHolder allows views to be cached for fast access
    open class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        var contentCollapsed: Boolean = true
        init {
            // TODO: Multiple view types
            binding.cardView.setOnClickListener {
                contentCollapsed = !contentCollapsed
                TransitionManager.beginDelayedTransition(binding.cardView)
                binding.postBody.visibility = if(contentCollapsed) View.GONE else View.VISIBLE

            }
        }
    }



    object PostBindingAdapter {
        @BindingAdapter("date")
        @JvmStatic
        fun bindDate(textView: TextView, date: Date){
            textView.text = DateUtils.getRelativeTimeSpanString(
                date.time,
                Calendar.getInstance().timeInMillis,
                0
            )
        }

    }
}

// Callback class for calculating differences between post objects
class PostItemDiffCallback: DiffUtil.ItemCallback<Post>(){
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.title == newItem.title && oldItem.body == newItem.body
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return newItem.postId == oldItem.postId
    }

}