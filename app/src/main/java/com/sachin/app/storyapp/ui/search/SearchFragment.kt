package com.sachin.app.storyapp.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.sachin.app.storyapp.R
import com.sachin.app.storyapp.core.util.afterTextChanged
import com.sachin.app.storyapp.databinding.FragmentSearchBinding
import com.sachin.app.storyapp.ui.publication.PublicationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private val binding: FragmentSearchBinding by viewBinding()
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEditText.afterTextChanged {
            viewModel.onQueryTextChanged(it?.toString())
        }
        val publicationAdapter = PublicationAdapter(onItemClick = { storyId ->
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToDetailFragment(storyId)
            )
        })
        binding.recyclerView.adapter = publicationAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.storyStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                publicationAdapter.submitList(it)
            }
        }
    }
}