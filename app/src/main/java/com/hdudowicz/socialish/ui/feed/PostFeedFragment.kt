package com.hdudowicz.socialish.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.adapters.PostFeedAdapter
import com.hdudowicz.socialish.databinding.FragmentNewsFeedBinding
import com.hdudowicz.socialish.ui.createpost.CreatePostActivity
import com.hdudowicz.socialish.viewmodels.PostFeedViewModel

/**
 * Fragment containing a list of the latest posts. A Fragment was used to use the bottom navigation bar in MainActivity
 * to switch screens.
 *
 * @constructor Create new post feed fragment
 */
class PostFeedFragment : Fragment() {
    // Make variables for the ViewModel, binding class and post list adapter class
    private val viewModel by lazy { ViewModelProvider(this).get(PostFeedViewModel::class.java) }
    private lateinit var binding: FragmentNewsFeedBinding
    private lateinit var postFeedAdapter: PostFeedAdapter

    /**
     * Overriding onCreateView to initialise the post feed UI and logic for the fragment.
     *
     * @param inflater to instantiate XML layouts into corresponding View objects
     * @param container the ViewGroup that contains this Fragment view
     * @param savedInstanceState Bundle object containing previous fragment state data
     * @return the root view of the Fragment
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Create fragment view with binding class and set binding variable.
        binding = FragmentNewsFeedBinding.inflate(layoutInflater, container, false)
        // Create click handler object for use with data binding in layout
        binding.handler = PostFeedClickHandler()
        // Show options menu items in toolbar
        setHasOptionsMenu(true)
        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        // Using linear layout manager as the post feed is a linear list
        binding.postList.layoutManager = LinearLayoutManager(context)

        // Observing post list LiveData and updating post list adapter with new lists
        viewModel.postListLiveData.observe(viewLifecycleOwner, { list ->
            postFeedAdapter.submitList(list)
            // Stop refresh animation once list is updated
            binding.swiperefresh.isRefreshing = false
        })

        // Registering adapter data observer on post feed adapter to scroll to top post when new posts are inserted
        postFeedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.postList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        // Setting listener for the swipe down refresh gesture to load new posts
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadNewPosts()
        }

        // Returning root view of the view binding
        return binding.root
    }

    /**
     * Show refreshing animation and load posts from Firebase
     */
    private fun loadPosts(){
        binding.swiperefresh.isRefreshing = true
        viewModel.loadNewPosts()
    }

    /**
     * Overriding onResume to load posts when the user first opens, re-enters or switches the displayed fragments in the app
     */
    override fun onResume() {
        super.onResume()

        loadPosts()
    }

    /**
     * Handle toolbar options menu item clicks
     *
     * @param item the options item selected
     * @return boolean if the click was handled
     */
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

    /**
     * Overriding onCreateOptionsMenu to inflate a custom menu instead of the default
     *
     * @param menu reference to toolbar menu for managing menu items
     * @param inflater class to instantiate XML menu files into menu objects
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

/**
 * Click handler class for views in the PostFeedFragment
 *
 * @constructor Create new click handler object
 */
class PostFeedClickHandler{
    /**
     * Action for add post floating action button click. Starts CreatePostActivity where the user can make a new post.
     *
     * @param view the view for which the function is a click action
     */
    fun createNewPost(view: View){
        val intent = Intent(view.context, CreatePostActivity::class.java)
        view.context.startActivity(intent)
    }
}