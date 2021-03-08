package com.hdudowicz.socialish.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.PostListItemBinding
import com.hdudowicz.socialish.utils.PostUtils.getContext
import java.util.*
import kotlin.collections.ArrayList

class PostFeedAdapter: RecyclerView.Adapter<PostFeedAdapter.PostViewHolder>() {
    private var mPostList: ArrayList<Post> = arrayListOf()

    init {
        val post = Post(
            "",
            "",
            "",
            "Test Post",
            "Body",
            true,
            Date(Calendar.getInstance().timeInMillis - 86400000)
        )
        mPostList.add(post)
        mPostList.add(post)
        mPostList.add(post)
    }

    fun setPostList(list: ArrayList<Post>){
        mPostList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Create post view using post list item layout binding
        val bind = PostListItemBinding.inflate(layoutInflater, parent, false)

        return PostViewHolder(bind)
    }

    // Overriding onBindViewHolder to initialise Post object databinding
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getPost(position)
        holder.binding.post = post
    }

    override fun getItemCount(): Int {
        return mPostList.size
    }

    fun getPost(pos: Int): Post {
        return mPostList[pos]
    }

    open class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            // TODO: Multiple view types 
        }
    }

    class ImagePostViewHolder(binding: PostListItemBinding): PostViewHolder(binding){
        init {
            binding.postImage.visibility = View.VISIBLE

            val progressDrawable = CircularProgressDrawable(binding.getContext())
            progressDrawable.strokeWidth = 4f
            progressDrawable.centerRadius = 25f
            progressDrawable.start()

            Glide.with(binding.getContext())
                .load("https://static.wikia.nocookie.net/dogelore/images/8/87/411.png/revision/latest/top-crop/width/360/height/450?cb=20200330152532")
                .centerCrop()
                .placeholder(progressDrawable)
                .into(binding.postImage)
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