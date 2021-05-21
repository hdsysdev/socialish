package com.hdudowicz.socialish.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.data.source.PostRepository
import com.hdudowicz.socialish.databinding.PostListItemBinding
import com.hdudowicz.socialish.util.ImageUtil
import com.perfomer.blitz.setTimeAgo
import java.util.*

// Using list adapter instead of RecyclerView adapter because it automates change detection in data set using DiskUtil thus reducing boilerplate code.
class PostFeedAdapter(): ListAdapter<Post, PostFeedAdapter.PostViewHolder>(PostItemDiffCallback()) {
    private val postRepository = PostRepository()
    var isLocal = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Create post view using post list item layout binding
        val bind = PostListItemBinding.inflate(layoutInflater, parent, false)

        return PostViewHolder(bind)
    }

    // Overriding onBindViewHolder to initialise Post object databinding
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        val viewBinding = holder.binding
        viewBinding.post = post

        val context = viewBinding.root.context

        // Using library for auto updating time string since post created
        viewBinding.postedDate.setTimeAgo(post.datePosted, showSeconds = false, autoUpdate = true)

        // Setting post image visibility to gone by default, visibility is changed for image posts later
        viewBinding.postImage.visibility = View.GONE

        if (post.isImagePost){
            viewBinding.postImage.visibility = View.VISIBLE

            // Creating progress drawable to show when Glide is loading a post
            val progressDrawable = CircularProgressDrawable(context).apply {
                strokeWidth = 6f
                centerRadius = 35f
            }
            progressDrawable.start()

            // Setting post image with glide.
            if (isLocal){
                // Get locally saved post images using ImageUtil function to get File object of the post image
                Glide.with(context)
                    .load(ImageUtil.getImageFile(context, post.postId))
                    .fitCenter()
                    .error(ColorDrawable(Color.BLACK))
                    .placeholder(progressDrawable)
                    .into(viewBinding.postImage)
            } else {
                Glide.with(context)
                    .load(post.imageUri)
                    .fitCenter()
                    .error(ColorDrawable(Color.BLACK))
                    .placeholder(progressDrawable)
                    .into(viewBinding.postImage)
            }
        } else {
            // Clearing glide load to prevent images being loaded in the wrong post due to recycling of views
            Glide.with(context).clear(viewBinding.postImage)
        }


        // If post body is empty then hide it's body TextView
        if (post.body.isBlank()){
            viewBinding.postBody.visibility = View.GONE
        } else {
            viewBinding.postBody.visibility = View.VISIBLE
        }

        // Listener for share button using android Sharesheet to share post text to other apps
        viewBinding.sharePost.setOnClickListener {
            // Creating share text
            val shareText = "Socialish Post - \nTitle: ${post.title} \nBody: ${post.body}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, post.title)
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

        val isPostSaved = postRepository.isPostSaved(context, post)
        loadSaveIconInView(viewBinding.savePost, isPostSaved)

        // When clicking save post button it checks if a post is saved locally and either saves or deletes it based on that then updates the save button icon
        viewBinding.savePost.setOnClickListener {
            var savedState = postRepository.isPostSaved(context, post)
            if (savedState){
                postRepository.deleteLocalPost(context, post)
                savedState = false
            } else {
                postRepository.savePostLocally(context, post)
                savedState = true
            }

            loadSaveIconInView(viewBinding.savePost, savedState)
        }
    }

    fun loadSaveIconInView(imageView: ImageView, saved: Boolean){
        if (saved){
            Glide.with(imageView.context)
                .load(R.drawable.ic_favorite_black_36dp)
                .fitCenter()
                .placeholder(R.drawable.ic_favorite_border_black_36dp)
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(R.drawable.ic_favorite_border_black_36dp)
                .fitCenter()
                .placeholder(R.drawable.ic_favorite_black_36dp)
                .into(imageView)

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