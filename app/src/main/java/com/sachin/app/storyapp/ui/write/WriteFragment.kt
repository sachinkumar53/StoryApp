package com.sachin.app.storyapp.ui.write

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteFragment : Fragment(R.layout.fragment_write) {
    private val binding: FragmentWriteBinding by viewBinding()
    private val viewModel: WriteViewModel by hiltNavGraphViewModels(R.id.nav_graph_compose)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiString.observe(viewLifecycleOwner) { uiState ->
            binding.publishButton.isEnabled = uiState.content.isNotBlank()
            binding.publishing.root.isVisible = uiState.isPublishing
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.storyField.apply {
            doAfterTextChanged {
                viewModel.onContentChange(it?.toString() ?: "")
            }
        }

        binding.publishButton.setOnClickListener {
            viewModel.onPublishClick {
                findNavController().popBackStack(R.id.coverFragment, true)
            }
        }

    }
}