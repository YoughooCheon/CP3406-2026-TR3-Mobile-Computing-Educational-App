package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.TopicGuide
import com.example.cp3406_assessment3_younghoocheon.data.TopicGuideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TopicGuideState {
    data object Loading : TopicGuideState
    data class Ready(val guide: TopicGuide) : TopicGuideState
    data object Failed : TopicGuideState
}

class TopicGuideViewModel(
    private val repository: TopicGuideRepository,
    private val topic: String,
) : ViewModel() {

    private val _state = MutableStateFlow<TopicGuideState>(TopicGuideState.Loading)
    val state: StateFlow<TopicGuideState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = TopicGuideState.Loading
        viewModelScope.launch {
            repository.load(topic).fold(
                onSuccess = { guide -> _state.value = TopicGuideState.Ready(guide) },
                onFailure = { _state.value = TopicGuideState.Failed },
            )
        }
    }
}
