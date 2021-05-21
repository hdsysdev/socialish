package com.hdudowicz.socialish.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.adapters.PostFeedAdapter
import com.hdudowicz.socialish.databinding.FragmentProfileBinding
import com.hdudowicz.socialish.viewmodels.ProfileViewModel

class ProfileFragment : Fragment(), TabLayout.OnTabSelectedListener {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var postFeedAdapter: PostFeedAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        profileViewModel =
                ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(context)
        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        binding.postList.layoutManager = layoutManager

        profileViewModel.postList.observe(viewLifecycleOwner, { posts ->
            postFeedAdapter.submitList(posts)

            if (profileViewModel.selectedTab == 1 && posts.size == 0){
                binding.postListEmpty.visibility = View.VISIBLE
            } else {
                binding.postListEmpty.visibility = View.GONE
            }
            layoutManager.scrollToPositionWithOffset(0, 0)
        })

        profileViewModel.displayName.observe(viewLifecycleOwner, { name ->
            binding.displayName.text = name
        })

        binding.tabs.addOnTabSelectedListener(this)


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout){
            profileViewModel.logoutUser()
            activity?.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if(profileViewModel.selectedTab == 0){
            postFeedAdapter.isLocal = false
            postFeedAdapter.submitList(listOf())
            profileViewModel.loadMyPosts()
            profileViewModel.loadDisplayName()
        } else {
            postFeedAdapter.isLocal = true
            profileViewModel.loadLocalPosts()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        if (tab.position == 0){
            postFeedAdapter.isLocal = false
            postFeedAdapter.submitList(listOf())
            profileViewModel.loadMyPosts()
        } else {
            postFeedAdapter.isLocal = true
            profileViewModel.loadLocalPosts()
        }
        profileViewModel.selectedTab = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
}