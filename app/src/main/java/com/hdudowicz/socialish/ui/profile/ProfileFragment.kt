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

        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        binding.postList.layoutManager = LinearLayoutManager(context)

        profileViewModel.postList.observe(viewLifecycleOwner, { posts ->
            postFeedAdapter.submitList(posts)
        })

        binding.tabs.addOnTabSelectedListener(this)

        Glide.with(binding.root.context)
            .load(profileViewModel.getProfilePicUri())
            .fitCenter()
            .error(ColorDrawable(Color.BLACK))
            .into(binding.profilePic)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.change_pic -> {
                //TODO: Change profile pic

                true
            }
            else -> false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()

        profileViewModel.loadMyPosts()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab?.position == 0){
            profileViewModel.loadMyPosts()
        } else {
            profileViewModel.loadLocalPosts()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
}