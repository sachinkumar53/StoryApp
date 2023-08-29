package com.sachin.app.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sachin.app.storyapp.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    val recommended = repository.getRecommended().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val topRated = repository.getTopRated().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val newReleases = repository.getNewReleases().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

}