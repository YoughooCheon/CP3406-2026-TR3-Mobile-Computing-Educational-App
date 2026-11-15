package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.AppSettings
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.SettingsStore
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import com.example.cp3406_assessment3_younghoocheon.data.StudyStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared by the Home, Practice, Progress and Profile screens: cards, statistics and settings in one place. */
class OverviewViewModel(
    private val repository: StudyRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    val stats: StateFlow<StudyStats> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StudyStats())

    val cards: StateFlow<List<Flashcard>> = repository.observeCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SampleData.cards)

    val settings: StateFlow<AppSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setDailyGoal(value: Int) {
        viewModelScope.launch { settingsStore.setDailyGoal(value) }
    }

    fun setCardsPerSession(value: Int) {
        viewModelScope.launch { settingsStore.setCardsPerSession(value) }
    }

    fun setNickname(value: String) {
        viewModelScope.launch { settingsStore.setNickname(value) }
    }
}