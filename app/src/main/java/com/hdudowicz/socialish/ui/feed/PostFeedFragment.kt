package com.hdudowicz.socialish.ui.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.adapters.PostFeedAdapter
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.FragmentNewsFeedBinding
import com.hdudowicz.socialish.ui.createpost.CreatePostActivity
import com.hdudowicz.socialish.viewmodels.PostFeedViewModel
import java.util.*
import kotlin.collections.ArrayList

class PostFeedFragment : Fragment() {

    private lateinit var binding: FragmentNewsFeedBinding
    private lateinit var viewModel: PostFeedViewModel
    private lateinit var postFeedAdapter: PostFeedAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        viewModel =
                ViewModelProvider(this).get(PostFeedViewModel::class.java)
        binding = FragmentNewsFeedBinding.inflate(layoutInflater, container, false)
        binding.handler = PostFeedClickHandler()
        setHasOptionsMenu(true)
        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        binding.postList.layoutManager = LinearLayoutManager(context)

        viewModel.postFeedLiveData.observe(viewLifecycleOwner, { list ->
            postFeedAdapter.submitList(list)
            // Stop refresh animation
            binding.swiperefresh.isRefreshing = false

        })

        // Registering adapter data observer on post feed adapter to scroll to top post when new post is inserted
        postFeedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.postList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        binding.createPostButton.setOnClickListener {
            startActivity(Intent(it.context, CreatePostActivity::class.java),)
        }


        // Listener for swipe down to refresh gesture
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadNewPosts()
        }

        return binding.root
    }

    fun loadPosts(){
        binding.swiperefresh.isRefreshing = true
        viewModel.loadNewPosts()
    }

    override fun onResume() {
        super.onResume()

        loadPosts()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.refresh -> {
                postFeedAdapter.submitList(listOf())
                loadPosts()

                true
            }
            else -> false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}

class PostFeedClickHandler{
    fun onAddPostClicked(view: View){
        val intent = Intent(view.context, CreatePostActivity::class.java)
        view.context.startActivity(intent)
    }
}