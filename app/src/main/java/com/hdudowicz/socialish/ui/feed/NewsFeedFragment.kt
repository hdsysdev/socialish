package com.hdudowicz.socialish.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.viewmodels.NewsFeedViewModel

class NewsFeedFragment : Fragment() {

    private lateinit var newsFeedViewModel: NewsFeedViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        newsFeedViewModel =
                ViewModelProvider(this).get(NewsFeedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news_feed, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        newsFeedViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}