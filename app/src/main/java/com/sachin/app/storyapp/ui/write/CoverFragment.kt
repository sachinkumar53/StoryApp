package com.sachin.app.storyapp.ui.write

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentCoverBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoverFragment : Fragment(R.layout.fragment_cover) {
    private val binding: FragmentCoverBinding by viewBinding()
    private val viewModel: WriteViewModel by hiltNavGraphViewModels(R.id.nav_graph_compose)
    private val galleryLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (uri != null) {
            context?.contentResolver?.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.onCoverSelected(bitmap)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiString.observe(viewLifecycleOwner) { uiState ->
            binding.coverImageView.setImageBitmap(uiState.cover)
            binding.addImageHintTextView.isVisible = uiState.cover == null
            binding.titleInputLayout.error = uiState.titleError
            binding.descriptionInputLayout.error = uiState.descriptionError
            binding.genreInputLayout.error = uiState.genreError
            binding.coverImageErrorTextview.text = uiState.coverError
            binding.coverImageErrorTextview.isVisible = uiState.coverError != null
        }

        binding.genreAutoCompleteField.apply {
            //setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            showSoftInputOnFocus = false
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    resources.getStringArray(R.array.genres).toList()
                )
            )

            doAfterTextChanged {
                viewModel.onGenreChanged(it?.toString() ?: "")
            }
        }


        binding.titleField.doAfterTextChanged {
            viewModel.onTitleChange(it?.toString() ?: "")
        }

        binding.descriptionField.doAfterTextChanged {
            viewModel.onDescriptionChange(it?.toString() ?: "")
        }

        binding.descriptionInputLayout.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.writeButton.setOnClickListener {
            if (viewModel.validate()) {
                findNavController().navigate(R.id.action_coverFragment_to_writeFragment)
            }
        }

        binding.coverCard.setOnClickListener {
            launchGallery()
        }
    }

    private fun launchGallery() {
        galleryLauncher.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        )
    }
}