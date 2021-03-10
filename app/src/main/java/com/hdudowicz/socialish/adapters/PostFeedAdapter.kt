package com.hdudowicz.socialish.adapters

import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.PostListItemBinding
import com.hdudowicz.socialish.utils.PostUtils.getContext
import java.util.*
import kotlin.collections.ArrayList

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

        holder.binding.postImage.visibility = View.VISIBLE

        val progressDrawable = CircularProgressDrawable(holder.binding.getContext())
        progressDrawable.strokeWidth = 4f
        progressDrawable.centerRadius = 25f
        progressDrawable.start()

        Glide.with(holder.binding.getContext())
            .load("https://static.wikia.nocookie.net/dogelore/images/8/87/411.png/revision/latest/top-crop/width/360/height/450?cb=20200330152532")
            .centerCrop()
            .placeholder(progressDrawable)
            .into(holder.binding.postImage)

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
        init {
            // TODO: Multiple view types 
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
        return newItem == oldItem
    }

}