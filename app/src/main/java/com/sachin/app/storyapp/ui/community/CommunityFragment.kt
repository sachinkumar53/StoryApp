package com.sachin.app.storyapp.ui.community

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.shareStoryLink
import com.sachin.app.storyapp.data.event.showToast
import com.sachin.app.storyapp.databinding.FragmentCommunityBinding
import com.sachin.app.storyapp.ui.dashboard.DashboardFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityFragment : Fragment(R.layout.fragment_community) {
    private val viewModel: CommunityViewModel by viewModels()
    private val binding: FragmentCommunityBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val communityListAdapter = CommunityListAdapter(
            onItemClick = { story ->
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                    .navigate(
                        DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(story.id)
                    )
            },
            onAddToLibraryClick = { story ->
                viewModel.addToLibrary(story)
            },
            onShareClick = { story ->
                shareStoryLink(storyId = story.id, coverUrl = story.coverUrl)
            }
        )

        binding.storyRecyclerView.adapter = communityListAdapter

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.progrssBar.isVisible = uiState.isLoading
            communityListAdapter.submitList(uiState.posts)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultEventFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { it.showToast(requireContext()) }
        }
    }
}
