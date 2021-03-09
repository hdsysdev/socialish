package com.hdudowicz.socialish.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdudowicz.socialish.adapters.PostFeedAdapter
import com.hdudowicz.socialish.data.model.Post
import com.hdudowicz.socialish.databinding.FragmentNewsFeedBinding
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
    ): View? {
        viewModel =
                ViewModelProvider(this).get(PostFeedViewModel::class.java)
        binding = FragmentNewsFeedBinding.inflate(layoutInflater, container, false)
//        val root = inflater.inflate(R.layout.fragment_news_feed, container, false)

//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        newsFeedViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        // Set adapter for populating the post feed recycler view
        postFeedAdapter = PostFeedAdapter()
        binding.postList.adapter = postFeedAdapter
        binding.postList.layoutManager = LinearLayoutManager(context)

        viewModel.postFeedLiveData.observe(viewLifecycleOwner, { list ->
            postFeedAdapter.submitList(list)
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val mPostList: ArrayList<Post> = arrayListOf()
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
        postFeedAdapter.submitList(mPostList)
    }
}