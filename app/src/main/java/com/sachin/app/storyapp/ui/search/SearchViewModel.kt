package com.sachin.app.storyapp.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.model.StoryBasic
import com.sachin.app.storyapp.data.repository.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PublicationRepository
) : ViewModel() {

    private val _storyStateFlow = MutableStateFlow<List<StoryBasic>>(emptyList())
    val storyStateFlow = _storyStateFlow.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryTextChanged(query: String?) {
        searchJob?.cancel()
        if (query.isNullOrBlank())
            _storyStateFlow.update { emptyList() }
        else {
            searchJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                Log.e(TAG, "onQueryTextChanged: ", throwable)
            }) {
                delay(500)
                val list = repository.queryStory(query)
                _storyStateFlow.update { list }
            }
        }
    }

}

private const val TAG = "SearchViewModel"