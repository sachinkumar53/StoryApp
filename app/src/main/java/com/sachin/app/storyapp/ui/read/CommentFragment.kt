package com.sachin.app.storyapp.ui.read

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.afterTextChanged
import com.sachin.app.storyapp.core.util.hideSoftKeyboard
import com.sachin.app.storyapp.databinding.FragmentCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentFragment : Fragment(R.layout.fragment_comment) {
    private val binding: FragmentCommentBinding by viewBinding()
    private val viewModel: CommentViewModel by viewModels()

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendButton.isEnabled = false
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.currentUser?.let {
            Glide.with(requireContext())
                .load(it.photoUrl)
                .circleCrop()
                .into(binding.userImageView)
        }

        val commentListAdapter = CommentListAdapter(onDeleteClick = viewModel::deleteComment)
        binding.recyclerView.adapter = commentListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.comments.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { list ->
                    commentListAdapter.submitList(list)
                }
        }

        binding.commentEditText.afterTextChanged {
            binding.sendButton.isEnabled = !it.isNullOrBlank()
        }

        binding.sendButton.setOnClickListener {
            val pd = ProgressDialog(requireActivity())
            pd.setMessage("Please wait")
            pd.show()
            val commentText = binding.commentEditText.text.toString()
            binding.commentEditText.text.clear()
            requireActivity().hideSoftKeyboard()
            viewModel.postComment(commentText) { pd.hide() }
        }

    }

}