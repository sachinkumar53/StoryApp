package com.sachin.app.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.data.event.showToast
import com.sachin.app.storyapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val binding: FragmentDetailBinding by viewBinding()
    private val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState is StoryDetailUiState.Success) {
                val detail = uiState.storyDetail
                binding.coverImageView.load(detail.coverUrl)
                binding.titleTextView.text = detail.title
                binding.publishDateTextView.text = detail.publishedDate
                binding.descriptionTextView.text = detail.description
                binding.authorTextView.text = detail.authorName
                binding.authorProfileImageView.load(detail.authorPhotoUrl) {
                    transformations(CircleCropTransformation())
                }
                binding.genreTextview.text = detail.genre

                binding.ratingTextView.text = if (detail.rating.ratingCount == 0) {
                    "--"
                } else {
                    "${
                        String.format(
                            "%.1f",
                            detail.rating.totalRating
                        )
                    } (${detail.rating.ratingCount})"
                }

            }

            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            binding.startReadingButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_detailFragment_to_readFragment,
                    Bundle().apply {
                        putString("story_id", arguments?.getString("story_id"))
                    })
            }

            binding.addToLibraryButton.setOnClickListener {
                viewModel.addToLibrary()
            }

            binding.shareButton.setOnClickListener {

            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.resultEventFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collectLatest { event ->
                        event.showToast(requireContext())
                    }
            }
        }
    }
}