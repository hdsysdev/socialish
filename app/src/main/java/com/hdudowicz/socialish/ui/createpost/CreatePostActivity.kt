package com.hdudowicz.socialish.ui.createpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityCreatePostBinding
import com.hdudowicz.socialish.viewmodels.CreatePostViewModel
import com.hdudowicz.socialish.viewmodels.LoginViewModel

class CreatePostActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePostBinding
    private val viewModel by lazy {ViewModelProvider(this).get(CreatePostViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.viewModel = viewModel

        viewModel.creatingPost.observe(this, {
            binding.progressBar.visibility = if (it) VISIBLE else GONE
        })

        binding.createPostButton.setOnClickListener {
            viewModel.createPost().observe(this, {
                if (it is Resource.Error) {
                    Toast.makeText(
                        this,
                        "Error creating post. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Post created",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            })
        }

    }

    override fun onSupportNavigateUp(): Boolean {

        finish()
        return true
    }
}