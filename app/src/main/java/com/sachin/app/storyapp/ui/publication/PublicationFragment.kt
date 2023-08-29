package com.sachin.app.storyapp.ui.publication

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentPublicationBinding
import com.sachin.app.storyapp.ui.dashboard.DashboardFragmentDirections
import com.sachin.app.storyapp.ui.library.LibraryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PublicationFragment : Fragment(R.layout.fragment_publication) {
    private val binding: FragmentPublicationBinding by viewBinding()
    private val viewModel: PublicationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val publicationAdapter = LibraryListAdapter(
            onItemClick = { story ->
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                    .navigate(
                        DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(story.id)
                    )
            },
            onMenuClick = { anchor, story ->
                PopupMenu(requireContext(), anchor).apply {
                    inflate(R.menu.menu_delete)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_delete -> {
                                val pd = ProgressDialog(requireActivity())
                                pd.setMessage("Please wait")
                                pd.show()
                                viewModel.deleteStory(story.id) { pd.hide() }
                                true
                            }

                            R.id.action_share -> true
                            else -> false
                        }
                    }
                }.show()
            }
        )

        binding.recyclerView.adapter = publicationAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState//.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { uiState ->
                    binding.progressBar.isVisible = uiState.isLoading
                    publicationAdapter.submitList(uiState.data)
                }
        }
    }
}