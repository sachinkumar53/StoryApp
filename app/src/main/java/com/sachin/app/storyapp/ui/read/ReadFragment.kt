package com.sachin.app.storyapp.ui.read

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentReadBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class ReadFragment : Fragment(R.layout.fragment_read), TextToSpeech.OnInitListener {
    private val binding: FragmentReadBinding by viewBinding()
    private val viewModel: ReadViewModel by viewModels()
    private val tts by lazy { TextToSpeech(requireContext(), this) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts.language = Locale.US

        binding.storyRating.notRatedView.setOnClickListener {
            findNavController().navigate(
                ReadFragmentDirections.actionReadFragmentToRateDialogFragment(viewModel.storyId)
            )
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_play_pause -> {
                    if (tts.isSpeaking) {
                        tts.stop()
                        item.setIcon(R.drawable.ic_play)
                    } else {
                        viewModel.uiState.value?.let {
                            if (it is ReadUiState.Success) {/*tts.synthesizeToFile(
                                    it.content,
                                    null,
                                    File(requireContext().externalCacheDir, "${it.title}.mp3"),
                                    null
                                )*/

                                tts.speak(
                                    it.content, TextToSpeech.QUEUE_FLUSH, null, null
                                )
                                item.setIcon(R.drawable.ic_pause)
                            }
                        }
                    }
                    true
                }

                else -> false
            }

        }

        viewModel.currentUser?.let { user ->
            Glide.with(requireContext()).load(user.photoUrl).circleCrop()
                .into(binding.storyRating.profileImageView)
            Log.e(TAG, "onViewCreated: ${user.photoUrl}")
            binding.storyRating.userNameTextview.text = user.displayName
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                ReadUiState.Loading -> {

                }

                is ReadUiState.Success -> {
                    binding.collapsingToolbar.title = uiState.title
                    binding.tv.text = uiState.content
                    binding.authorNameView.text = uiState.authorName
                    Glide.with(requireContext()).load(uiState.authorImageUrl).circleCrop()
                        .into(binding.authorImageView)
                }
            }
        }

        viewModel.ratingUiState.observe(viewLifecycleOwner) { uiState ->
            binding.storyRating.ratedView.isVisible = uiState.myRating > 0
            binding.storyRating.notRatedView.isVisible = uiState.myRating == 0

            binding.storyRating.myRatingBar.rating = uiState.myRating.toFloat()
            binding.storyRating.totalRatingTextview.text = uiState.totalRating.toString()
            binding.storyRating.totalRatingCountTextview.text =
                getString(R.string.total_rating_format, uiState.ratingCount.toString())
            binding.storyRating.moreButton.isVisible = uiState.myRating != 0
        }

        viewModel.topCommentFlow.observe(viewLifecycleOwner) { comment ->
            binding.latestComment.run {
                if (comment == null) {
                    this.expandButton.isVisible = false
                    this.addCommentPlaceholder.isVisible = true
                    Glide.with(requireContext())
                        .load(viewModel.currentUser?.photoUrl)
                        .placeholder(R.drawable.ic_user_filled).circleCrop()
                        .into(binding.latestComment.latestCommentUserImage)
                } else {
                    this.addCommentPlaceholder.isVisible = false
                    this.expandButton.isVisible = true
                    Glide.with(requireContext())
                        .load(comment.photoUrl)
                        .placeholder(R.drawable.ic_user_filled).circleCrop()
                        .into(binding.latestComment.latestCommentUserImage)
                    this.latestCommentUserName.text = comment.username
                    this.latestCommentTextview.text = comment.text
                }
            }
        }

        binding.latestComment.root.setOnClickListener {
            findNavController().navigate(
                ReadFragmentDirections.actionReadFragmentToCommentFragment(
                    viewModel.storyId
                )
            )
        }

        binding.storyRating.moreButton.setOnClickListener { v ->
            PopupMenu(requireActivity(), v).apply {
                menuInflater.inflate(R.menu.rating_menu, menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            viewModel.deleteRating()
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }

    }

    override fun onInit(status: Int) {
        Log.e(TAG, "onInit: Status = $status")
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}

private const val TAG = "ReadFragment"