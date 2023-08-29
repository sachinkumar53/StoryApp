package com.sachin.app.storyapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentHomeBinding
import com.sachin.app.storyapp.ui.dashboard.DashboardFragmentDirections
import com.sachin.app.storyapp.ui.publication.PublicationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recommendedAdapter = PublicationAdapter(onItemClick = ::onItemClick)
        val topRatedAdapter = PublicationAdapter(onItemClick = ::onItemClick)
        val newReleasesAdapter = PublicationAdapter(onItemClick = ::onItemClick)

        binding.recyclerViewRecommended.adapter = recommendedAdapter
        binding.recyclerViewTopRated.adapter = topRatedAdapter
        binding.recyclerViewNewReleases.adapter = newReleasesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recommended.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { list ->
                    recommendedAdapter.submitList(list)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.topRated.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { list ->
                    topRatedAdapter.submitList(list)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newReleases.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { list ->
                    newReleasesAdapter.submitList(list)
                }
        }

    }

    private fun onItemClick(storyId: String) {
        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            .navigate(DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(storyId))
    }

}