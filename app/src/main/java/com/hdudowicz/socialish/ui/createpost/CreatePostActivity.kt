package com.hdudowicz.socialish.ui.createpost

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.hdudowicz.socialish.R
import com.hdudowicz.socialish.databinding.ActivityCreatePostBinding
import com.hdudowicz.socialish.viewmodels.CreatePostViewModel
import es.dmoral.toasty.Toasty


/**
 * Activity for making new text or image posts. Uses ClickHandler and data binding instead of
 * programmatically setting onClick listeners.
 *
 * @constructor Create empty Create post activity
 */
class CreatePostActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreatePostBinding
    private val viewModel by lazy {ViewModelProvider(this).get(CreatePostViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setting ViewModel and click handler to use with data binding
        binding.viewModel = viewModel
        binding.handler = CreatePostClickHandler()

        // Observing isCreatingPost LiveData to display progress bar when set to true
        viewModel.isCreatingPost.observe(this, {
            binding.progressBar.visibility = if (it) VISIBLE else GONE
        })

        // Observe imageUri live data to load selected image into preview ImageView
        viewModel.imageUri.observe(this, { uri ->
            // Create progress bar drawable for a placeholder while Glide is loading an image
            val progressDrawable = CircularProgressDrawable(baseContext)
            progressDrawable.strokeWidth = 4f
            progressDrawable.centerRadius = 25f
            progressDrawable.start()

            // If image URI exists then load the image into the preview ImageView and set image name TextView text
            if (uri != null){
                Glide.with(baseContext)
                    .load(uri)
                    .fitCenter()
                    .placeholder(progressDrawable)
                    .into(binding.imagePreview)

                binding.imageName.text = uri.path
            }
        })
    }

    /**
     * Overriding onActivityResult to get the selected image's URI from the image gallery
     *
     * @param requestCode Result code indicating if the operation was successful
     * @param resultCode code to identify different activity results
     * @param data returned by the image gallery containing the selected image URI
     */
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

    /**
     * Finishes activity when the toolbar back button is pressed.
     *
     * @return boolean indicating if the back button event was handled
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * ClickHandler class for CreatePostActivity with functions to be ran when clicking buttons in the Activity.
     *
     * @constructor Create new click handler object
     */
    inner class CreatePostClickHandler{
        /**
         * Validates if the title is blank and shows an error if so otherwise creates a new post on Firestore.
         * Shows a toast message depending on if creating the post was successful.
         *
         * @param view the View for which the function is the click action
         */
        fun createPost(view: View){
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
                // If title is not blank then create post and finish the activity
                viewModel.createPost().observe(this@CreatePostActivity, { success ->
                    // Show toast alerting user if creating the post was successful
                    if (success) {
                        Toasty.success(view.context, "Post created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(view.context, "Error creating post. Try again later.", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                })
            }
        }

        /**
         * Opens default image gallery app to select a single image for the post
         *
         * @param view the Button view for which this function is the click action
         */
        fun selectImage(view: View){
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(i, "Select Image"), 1)
        }
    }
}