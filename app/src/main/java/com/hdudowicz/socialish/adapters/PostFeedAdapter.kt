package com.hdudowicz.socialish.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.text.format.DateUtils
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
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.PostListItemBinding
import com.perfomer.blitz.setTimeAgo
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

        // Using library for auto updating time string since post created
        holder.binding.postedDate.setTimeAgo(post.datePosted, showSeconds = false, autoUpdate = true)

        // Setting post image visibility to gone by default, visibility is changed for image posts later
        holder.binding.postImage.visibility = View.GONE

        if (post.isImagePost){
            holder.binding.postImage.visibility = View.VISIBLE

            // Creating progress drawable to show when Glide is loading a post
            val progressDrawable = CircularProgressDrawable(holder.binding.root.context).apply {
                strokeWidth = 6f
                centerRadius = 35f
            }
            progressDrawable.start()

            // Setting post image with glide
            Glide.with(holder.binding.root.context)
                .load(post.imageUri)
                .fitCenter()
                .error(ColorDrawable(Color.BLACK))
                .placeholder(progressDrawable)
                .into(holder.binding.postImage)
        } else {
            // Clearing glide load to prevent images being loaded in the wrong post due to recycling of views
            Glide.with(holder.binding.root.context).clear(holder.binding.postImage)
        }


        // If post body is empty then hide it's body TextView
        if (post.body.isBlank()){
            holder.binding.postBody.visibility = View.GONE
        } else {
            holder.binding.postBody.visibility = View.VISIBLE
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


    // Using a ViewHolder allows views to be cached instead of recreating them
    class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        var bodyCollapsed: Boolean = true
        init {
            // Clicking the post card reveals the rest of the post body
            binding.cardView.setOnClickListener {
                bodyCollapsed = !bodyCollapsed
                TransitionManager.beginDelayedTransition(binding.cardView)
                // Change max lines showing depending on if body is collapsed
                if (bodyCollapsed){
                    binding.postBody.maxLines = 1
                } else {
                    binding.postBody.maxLines = 5
                }
            }
        }
    }
}

// Callback class for calculating differences between post objects
class PostItemDiffCallback: DiffUtil.ItemCallback<Post>(){
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.title == newItem.title && oldItem.body == newItem.body && oldItem.imageUri == newItem.imageUri
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return newItem.postId == oldItem.postId
    }

}