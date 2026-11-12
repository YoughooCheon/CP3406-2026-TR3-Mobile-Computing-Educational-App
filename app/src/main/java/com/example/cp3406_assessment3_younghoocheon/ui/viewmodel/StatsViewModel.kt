package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import com.example.cp3406_assessment3_younghoocheon.data.StudyStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(repository: StudyRepository) : ViewModel() {
    val stats: StateFlow<StudyStats> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StudyStats())

    val cards: StateFlow<List<Flashcard>> = repository.observeCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SampleData.cards)
}