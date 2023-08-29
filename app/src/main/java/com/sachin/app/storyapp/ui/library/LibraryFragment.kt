package com.sachin.app.storyapp.ui.library

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentLibraryBinding
import com.sachin.app.storyapp.ui.dashboard.DashboardFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val viewModel: LibraryViewModel by viewModels()
    private val binding: FragmentLibraryBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LibraryListAdapter(
            onItemClick = { story ->
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                    .navigate(
                        DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(story.id)
                    )
            },
            onMenuClick = { anchor, story ->
                PopupMenu(requireContext(), anchor).apply {
                    inflate(R.menu.library_item_menu)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_remove_from_library -> {
                                val pd = ProgressDialog(requireActivity())
                                pd.setMessage("Please wait")
                                pd.show()
                                viewModel.removeFromLibrary(story.id) { pd.hide() }
                                true
                            }

                            R.id.action_share -> true
                            else -> false
                        }
                    }
                }.show()
            }
        )
        binding.libraryRecyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.itemsFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                adapter.submitList(it)
            }
        }
    }
}