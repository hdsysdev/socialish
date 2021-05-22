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

/**
 * Adapter class for populating a RecyclerView with Post objects
 * List adapter used instead of RecyclerView adapter because it automates change detection in data set using DiskUtil, reducing boilerplate code.
 *
 * @constructor Create empty Post feed adapter
 */
class PostFeedAdapter(): ListAdapter<Post, PostFeedAdapter.PostViewHolder>(PostItemDiffCallback()) {
    private val postRepository = PostRepository()
    var isLocal = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Create post item view using the PostListItemBinding generated binding class
        val bind = PostListItemBinding.inflate(layoutInflater, parent, false)
        // Returning new view holder with post item binding
        return PostViewHolder(bind)
    }

    // Overriding onBindViewHolder to initialise Post item view
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Getting current Post object by list item position
        val post = getItem(position)
        val viewBinding = holder.binding
        // Setting binding post object to use data binding
        viewBinding.post = post

        val context = viewBinding.root.context

        // Using library for an auto updating string showing the time since the post was created
        viewBinding.postedDate.setTimeAgo(post.datePosted, showSeconds = false, autoUpdate = true)

        // Setting post image visibility to gone by default, visibility is changed for image posts later
        viewBinding.postImage.visibility = View.GONE

        // Load image for this post item
        loadPostImage(post, viewBinding)

        // If post body is empty then hide post body TextView
        if (post.body.isBlank()){
            viewBinding.postBody.visibility = View.GONE
        } else {
            viewBinding.postBody.visibility = View.VISIBLE
        }

        // Listener for share button using android Sharesheet to share post text to other apps
        viewBinding.sharePost.setOnClickListener {
            // Creating share text
            val shareText = "Socialish Post - \nTitle: ${post.title} \nBody: ${post.body}"
            // Creating intent for sharing text data with other apps, includes a title and text
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, post.title)
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            // Opening Android Sharesheet app chooser
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

        // Checking if this post is saved locally and loading the appropriate save button icon
        val isPostSaved = postRepository.isPostSaved(context, post)
        loadSaveIconInView(viewBinding.savePost, isPostSaved)

        // Clicking the save post button checks if a post is saved locally, either saves or deletes it then updates the save button icon
        viewBinding.savePost.setOnClickListener {
            var savedState = postRepository.isPostSaved(context, post)
            if (savedState){
                postRepository.deleteLocalPost(context, post)
                savedState = false
            } else {
                postRepository.savePostLocally(context, post)
                savedState = true
            }

            // Loading new save button icon into view
            loadSaveIconInView(viewBinding.savePost, savedState)
        }
    }

    /**
     * If the passed Post object is an image post, it loads the image into the passed post item's ImageView.
     * Checks if the post is in local storage or the internet and loads the image appropriately.
     *
     * @param post Post object to load an image for
     * @param viewBinding View binding class for the post list item corresponding to the passed post
     */
    private fun loadPostImage(post: Post, viewBinding: PostListItemBinding){
        val context = viewBinding.root.context
        // If the current post is an image post then show ImageView and initialise with image
        if (post.isImagePost){
            viewBinding.postImage.visibility = View.VISIBLE

            // Creating circular progress drawable to display in ImageView when Glide is loading a post image
            val progressDrawable = CircularProgressDrawable(context).apply {
                strokeWidth = 6f
                centerRadius = 35f
            }
            progressDrawable.start()

            // Setting post image with glide depending on if this adapter is showing locally stored posts
            if (isLocal){
                // Get locally saved post images using ImageUtil function to get File object of the post image
                Glide.with(context)
                    .load(ImageUtil.getImageFile(context, post.postId))
                    .fitCenter()
                    .error(ColorDrawable(Color.BLACK))
                    .placeholder(progressDrawable)
                    .into(viewBinding.postImage)
            } else {
                // If the post is not local then load the image from a URI on the internet
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
    }

    /**
     * Loads the appropriate save button icon into passed ImageView depending on if a post is saved.
     *
     * @param imageView view to change the icon of
     * @param saved saved state of the current post item view
     */
    private fun loadSaveIconInView(imageView: ImageView, saved: Boolean){
        if (saved){
            Glide.with(imageView.context)
                .load(R.drawable.ic_baseline_save_24)
                .fitCenter()
                .placeholder(R.drawable.ic_outline_save_24)
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(R.drawable.ic_outline_save_24)
                .fitCenter()
                .placeholder(R.drawable.ic_baseline_save_24)
                .into(imageView)

        }
    }


    /**
     * ViewHolder class containing post card view click listener and item binding class.
     * It allows views to be cached instead of recreating them
     *
     * @property binding
     *
     * @constructor Create list item ViewHolder and initialise expand post card on click
     */
    class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        var bodyCollapsed: Boolean = true
        init {
            // Clicking the post card reveals the rest of the post body
            binding.cardView.setOnClickListener {
                bodyCollapsed = !bodyCollapsed
                TransitionManager.beginDelayedTransition(binding.cardView)
                // Change max lines showing depending on if post body is collapsed
                if (bodyCollapsed){
                    binding.postBody.maxLines = 1
                } else {
                    binding.postBody.maxLines = 5
                }
            }
        }
    }
}

/**
 * Callback class for calculating differences between post objects
 *
 * @constructor Create DiffUtil.ItemCallback class for comparing Post objects
 */
class PostItemDiffCallback: DiffUtil.ItemCallback<Post>(){
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.title == newItem.title && oldItem.body == newItem.body && oldItem.imageUri == newItem.imageUri
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return newItem.postId == oldItem.postId
    }
}