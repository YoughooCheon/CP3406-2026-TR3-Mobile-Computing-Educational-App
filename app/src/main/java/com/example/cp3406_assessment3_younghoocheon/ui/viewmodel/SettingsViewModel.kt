package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.AppSettings
import com.example.cp3406_assessment3_younghoocheon.data.SettingsStore
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val store: SettingsStore,
    private val repository: StudyRepository,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = store.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setDailyReminder(value: Boolean) {
        viewModelScope.launch { store.setDailyReminder(value) }
    }

    fun setBreakReminders(value: Boolean) {
        viewModelScope.launch { store.setBreakReminders(value) }
    }

    fun setHighContrast(value: Boolean) {
        viewModelScope.launch { store.setHighContrast(value) }
    }

    fun setReduceMotion(value: Boolean) {
        viewModelScope.launch { store.setReduceMotion(value) }
    }

    fun setTextSize(value: String) {
        viewModelScope.launch { store.setTextSize(value) }
    }

    fun deleteAll() {
        viewModelScope.launch { repository.deleteAll() }
    }

    suspend fun exportJson(): String = repository.exportJson()
}