package com.hdudowicz.socialish.ui.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.adapters.PostFeedAdapter
import com.hdudowicz.socialish.databinding.FragmentProfileBinding
import com.hdudowicz.socialish.util.DialogUtil
import com.hdudowicz.socialish.viewmodels.ProfileViewModel

/**
 * Fragment showing the logged in users details along with lists of their own posts and posts saved
 * on the device. Implements OnTabSelectedListener interface to handle changes in selected tab.
 *
 * @constructor Create a new profile fragment
 */
class ProfileFragment : Fragment(), TabLayout.OnTabSelectedListener {
    // Create variables for binding class, post list adapter and ViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var postFeedAdapter: PostFeedAdapter
    private val profileViewModel by lazy {ViewModelProvider(this).get(ProfileViewModel::class.java) }

    /**
     * Initialises the ProfileFragment view and logic
     *
     * @param inflater for instantiating XML layouts into View objects
     * @param container the ViewGroup containing this fragment
     * @param savedInstanceState Bundle of previous fragment state data
     * @return root view of the FragmentProfileBinding class
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate fragment views from generated binding class
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        // Show toolbar options menu
        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(context)
        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        binding.postList.layoutManager = layoutManager

        // Observe if the adapter requires posts to be reloaded and reload when required
        postFeedAdapter.shouldReload.observe(viewLifecycleOwner, { shouldReload ->
            loadPostsByTab()
        })

        // Observe for updates to the post list, update the displayed posts and scroll to the top post
        profileViewModel.postList.observe(viewLifecycleOwner, { posts ->
            postFeedAdapter.submitList(posts)

            // If displayed post list is empty then show empty message according to the selected tab
            if (profileViewModel.selectedTab == 0 && posts.size == 0){
                binding.postListEmpty.text = getString(R.string.no_posts_written)
                binding.postListEmpty.visibility = View.VISIBLE
            } else if (profileViewModel.selectedTab == 1 && posts.size == 0){
                binding.postListEmpty.text = getString(R.string.no_posts_saved)
                binding.postListEmpty.visibility = View.VISIBLE
            } else {
                binding.postListEmpty.visibility = View.GONE
            }
            // Scroll to the top post
            layoutManager.scrollToPositionWithOffset(0, 0)

            // Hiding progress bar after posts are loaded
            binding.progressIndicator.visibility = View.GONE
        })

        // Observe the display name stored in the ViewModel and update the display name text view
        profileViewModel.displayName.observe(viewLifecycleOwner, { name ->
            binding.displayName.text = name
        })

        // Adding this class to be a listener for changes in the selected tab
        binding.tabs.addOnTabSelectedListener(this)

        // Setting default selected tab
        postFeedAdapter.selectedTab = 0

        // Return root view of the inflated view binding class
        return binding.root
    }

    /**
     * Overriding onCreateOptionsMenu to inflate a custom menu instead of the default.
     *
     * @param menu interface for managing items in a menu
     * @param inflater used for inflating menu XML files into Menu objects
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handling options item selection. Logout item displays a logout confirmation dialog.
     *
     * @param item the selected menu item
     * @return result of calling onOptionsItemSelected in the super class
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout){
            DialogUtil.showLogoutDialog(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When the application resumes, a progress bar is shown and the user's display name and list of
     * posts are loaded depending on the selected tab.
     */
    override fun onResume() {
        super.onResume()
        // Showing progress bar while refreshing posts
        binding.progressIndicator.visibility = View.VISIBLE
        profileViewModel.loadDisplayName()

        loadPostsByTab()
    }

    /**
     * Loads either the user's own posts or the locally stored post list depending on what tab is selected.
     */
    private fun loadPostsByTab(){
        if (profileViewModel.selectedTab == 0){
            postFeedAdapter.submitList(listOf())
            profileViewModel.loadMyPosts()
        } else {
            profileViewModel.loadLocalPosts()
            postFeedAdapter.notifyDataSetChanged()
        }
    }

    /**
     * When the selected tab is changed, store the selected tab number in the list adapter and ViewModel
     * then load the post list depending on the newly selected tab
     *
     * @param tab the selected tab
     */
    override fun onTabSelected(tab: TabLayout.Tab) {
        // Showing progress bar while loading posts
        binding.progressIndicator.visibility = View.VISIBLE

        profileViewModel.selectedTab = tab.position
        postFeedAdapter.selectedTab = tab.position

        loadPostsByTab()
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
}