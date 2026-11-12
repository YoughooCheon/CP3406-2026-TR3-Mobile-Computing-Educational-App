package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.VideoInfo
import com.example.cp3406_assessment3_younghoocheon.data.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LearnState {
    data object Loading : LearnState
    data class Success(val info: VideoInfo) : LearnState
    data object Failed : LearnState
}

class LearnViewModel(
    private val repository: VideoRepository,
    private val videoUrl: String,
) : ViewModel() {

    private val _state = MutableStateFlow<LearnState>(LearnState.Loading)
    val state: StateFlow<LearnState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = LearnState.Loading
        viewModelScope.launch {
            repository.fetch(videoUrl).fold(
                onSuccess = { info -> _state.value = LearnState.Success(info) },
                onFailure = { _state.value = LearnState.Failed },
            )
        }
    }
}