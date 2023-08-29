package com.sachin.app.storyapp.ui.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.databinding.FragmentAdminBinding
import com.sachin.app.storyapp.ui.library.LibraryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminFragment : Fragment(R.layout.fragment_admin) {
    private val binding: FragmentAdminBinding by viewBinding()
    private val viewModel: AdminViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val publicationAdapter = LibraryListAdapter(
            onItemClick = { story ->
                findNavController().navigate(
                    AdminFragmentDirections.actionAdminFragmentToDetailFragment(
                        story.id
                    )
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
            viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { uiState ->
                    binding.progressBar.isVisible = uiState.isLoading
                    publicationAdapter.submitList(uiState.data)
                }
        }

        binding.logoutImageview.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    findNavController().navigate(
                        R.id.loginFragment,
                        null,
                        NavOptions.Builder().setPopUpTo(R.id.adminFragment, inclusive = true)
                            .build()
                    )
                }.setNegativeButton(R.string.no) { _, _ -> }
                .setCancelable(false)
                .show()
        }
    }
}