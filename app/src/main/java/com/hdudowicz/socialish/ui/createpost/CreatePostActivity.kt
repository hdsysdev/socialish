package com.hdudowicz.socialish.ui.createpost

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.data.model.Resource
import com.hdudowicz.socialish.databinding.ActivityCreatePostBinding
import com.hdudowicz.socialish.viewmodels.CreatePostViewModel
import es.dmoral.toasty.Toasty


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

            // If title field is blank then show error text
            if (viewModel.isTitleBlank()){
                binding.titleTextField.error = getString(R.string.title_blank_error)

                // Watch for title field changes and show or hide error if title is blank or not
                binding.titleInput.addTextChangedListener{ text: Editable? ->
                    if (!text.isNullOrBlank())
                        binding.titleTextField.error = null
                    else
                        binding.titleTextField.error = getString(R.string.title_blank_error)
                }
            } else {
                // If title is not blank then create post
                viewModel.createPost().observe(this, { success ->
                    if (success) {
                        Toasty.success(this, "Post created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(this, "Error creating post. Try again later.", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                })
            }
        }

        binding.addImageButton.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(i, "Select Picture"), 1)
        }

        // Observe imageUri live data to fill image preview view
        viewModel.imageUri.observe(this, { uri ->
            // Create progress bar drawable
            val progressDrawable = CircularProgressDrawable(baseContext)
            progressDrawable.strokeWidth = 4f
            progressDrawable.centerRadius = 25f
            progressDrawable.start()

            if (uri != null){
                Glide.with(baseContext)
                    .load(uri)
                    .fitCenter()
                    .placeholder(progressDrawable)
                    .into(binding.imagePreview)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            if (requestCode == 1){
                // If an image is selected, store the image Uri in the ViewModel
                val imageUri = data!!.data
                if (imageUri != null){
                    viewModel.imageUri.postValue(imageUri)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()
        return true
    }
}